package cronner.jfaster.org.job.schedule;

import com.google.common.base.Joiner;
import cronner.jfaster.org.enums.TaskStatus;
import cronner.jfaster.org.executor.ScheduleJobFacade;
import cronner.jfaster.org.executor.ShardingContexts;
import cronner.jfaster.org.pojo.TaskExecuteInfo;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;
import cronner.jfaster.org.service.TaskService;
import cronner.jfaster.org.util.http.Http;
import cronner.jfaster.org.util.http.HttpMethod;
import cronner.jfaster.org.util.json.GsonFactory;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;
import java.util.Map;

import static cronner.jfaster.org.constants.CronnerConstant.EXECUTE_URL;

/**
 * 调度作业.
 * @author fangyanpeng
 */
@Slf4j
public final class CronnerScheduleJob implements Job {

    @Setter
    private ScheduleJobFacade jobFacade;

    @Setter
    private TaskService taskService;

    @Setter
    private int jobId;
    
    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {
        //获取每个执行节点的分片信息
        CoordinatorRegistryCenter registryCenter = JobRegistry.getInstance().getRegCenter(jobFacade.getJobName());
        ScheduleService  scheduleService = new ScheduleService(registryCenter,jobFacade.getJobName());
        scheduleService.setScheduleFlag();
        scheduleService.leaderSchedule(new ScheduleService.ScheduleDistributor() {
            @Override
            public void schedule() {
                try {
                    Map<String,ShardingContexts> scheduleShardingContexts = jobFacade.scheduleShardingContexts();
                    if(scheduleShardingContexts.isEmpty()){
                        /**
                         * 没有可用的执行节点，直接返回
                         */

                        return;
                    }
                    for (Map.Entry<String,ShardingContexts> contextsEntry : scheduleShardingContexts.entrySet()){
                        String host = contextsEntry.getKey();
                        ShardingContexts contexts = contextsEntry.getValue();
                        if(contexts.getShardingItemParameters().isEmpty()){
                            /**
                             * 执行节点数大于分片数，导致部分执行节点没有分片
                             */
                            return;
                        }
                        int taskId = -1;
                        try {
                            //一个节点所有分片的执行信息，用于记录作业执行的轨迹
                            TaskExecuteInfo info = new TaskExecuteInfo();
                            Date now = new Date();
                            info.setCreateTime(now);
                            info.setJobName(jobFacade.getJobName());
                            info.setStatus(TaskStatus.READY.getFlag());
                            info.setJobId(jobId);
                            String items = Joiner.on(",").join(contexts.getShardingItemParameters().keySet());
                            info.setShardItems(items);
                            int parentId = taskService.addTask(info);
                            contexts.setTaskId(parentId);
                            //一个节点每个分片的执行信息
                            for (Integer item : contexts.getShardingItemParameters().keySet()){
                                info.setCreateTime(now);
                                info.setJobName(jobFacade.getJobName());
                                info.setStatus(TaskStatus.READY.getFlag());
                                info.setJobId(jobId);
                                info.setShardItems(String.valueOf(item));
                                info.setParentId(parentId);
                                taskId = taskService.addTask(info);
                                contexts.addItemTaskId(item,taskId);
                            }
                            //向执行节点发送任务，用于记录每个分片的执行状态
                            Http http = new Http(String.format(EXECUTE_URL,host));
                            http.method(HttpMethod.PUT);
                            http.contentType(Http.JSON_CONNTENT_TYPE);
                            http.body(GsonFactory.getGson().toJson(contexts));
                            http.request();
                        } catch (Throwable e) {
                            log.error("Schedule job: "+jobFacade.getJobName()+" fail,execute node host: "+host,e);
                            if(taskId != -1){
                                //TODO 异常添加数据库
                            }
                        }
                    }
                } catch (Throwable e) {
                    log.error("Schedule job fail,jobName:"+jobFacade.getJobName(),e);
                }
            }
        });

    }
}
