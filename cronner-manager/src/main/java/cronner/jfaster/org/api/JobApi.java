package cronner.jfaster.org.api;

import com.google.common.base.Joiner;
import cronner.jfaster.org.constants.CronnerConstant;
import cronner.jfaster.org.enums.JobStatus;
import cronner.jfaster.org.executor.JobCompleteHandler;
import cronner.jfaster.org.job.config.ConfigurationService;
import cronner.jfaster.org.job.election.LeaderService;
import cronner.jfaster.org.job.instance.ExecuteInstanceService;
import cronner.jfaster.org.job.instance.InstanceOperation;
import cronner.jfaster.org.job.operation.OperationService;
import cronner.jfaster.org.job.schedule.JobScheduler;
import cronner.jfaster.org.job.server.ServerStatus;
import cronner.jfaster.org.model.JobConfiguration;
import cronner.jfaster.org.model.JsonResponse;
import cronner.jfaster.org.pojo.BriefJobConfig;
import cronner.jfaster.org.pojo.JobConfig;
import cronner.jfaster.org.pojo.PageParam;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;
import cronner.jfaster.org.service.DetailService;
import cronner.jfaster.org.service.JobService;
import cronner.jfaster.org.service.TaskService;
import cronner.jfaster.org.util.BeanConfigCopyUtil;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Strings;
import org.jfaster.mango.transaction.TransactionAction;
import org.jfaster.mango.transaction.TransactionStatus;
import org.jfaster.mango.transaction.TransactionTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 *
 * 作业添加、修改、删除、查看、停止等api
 * @author fangyanpeng
 */
@Slf4j
@RestController
@RequestMapping("/job")
public class JobApi {

    @Resource
    private JobService jobService;
    
    @Resource
    private TaskService taskService;

    @Resource
    private JobCompleteHandler handler;

    @Resource
    private DetailService detailService;

    @Resource
    private CoordinatorRegistryCenter registryCenter;
    
    @Value("${server.port}")
    private int serverPort;

    @RequestMapping(value = "/add",method = RequestMethod.PUT)
    public JsonResponse addJob(@RequestBody final JobConfig config){
        //判断cron和dependency
        if(Strings.isNullOrEmpty(config.getDependency()) && Strings.isNullOrEmpty(config.getCron())){
            return JsonResponse.notOk("Dependency or cron need to be configured,and only one be configured.");
        }else if(!Strings.isNullOrEmpty(config.getDependency()) && !Strings.isNullOrEmpty(config.getCron())){
            return JsonResponse.notOk("Dependency and cron only one can be configured");
        }
        //检查作业是否存在
        JobConfig existJob = jobService.getJobByName(config.getJobName());
        if(existJob != null){
            return JsonResponse.notOk(String.format("'%s' is exist",config.getJobName()));
        }
        //检查依赖的作业是否存在
        if(!Strings.isNullOrEmpty(config.getDependency())){
            existJob = jobService.getJobByName(config.getDependency());
            if(existJob == null){
                return JsonResponse.notOk("Dependency job not exists!");
            }
        }
        final JobConfiguration configuration = BeanConfigCopyUtil.copy(JobConfiguration.class,config,JobConfig.class);
        Date now = new Date();
        config.setCreateTime(now);
        config.setUpdateTime(now);
        config.setStatus(true);
        try {
            TransactionTemplate.execute(CronnerConstant.DB, new TransactionAction() {
                @Override
                public void doInTransaction(TransactionStatus status) {
                    //添加配置到数据库
                    int jobId = jobService.addJob(config);
                    configuration.setId(jobId);
                    JobScheduler jobScheduler = new JobScheduler(registryCenter,configuration,serverPort,taskService,handler);
                    jobScheduler.init();
                }
            });
            LeaderService leaderService = new LeaderService(registryCenter,config.getJobName());
            leaderService.notifyOtherScheduler(configuration);
        } catch (Exception e) {
            log.error("Add job error: ",e);
            try {
                OperationService operationService = new OperationService(registryCenter,config.getJobName());
                operationService.operation(InstanceOperation.SHUTDOWN);
            } catch (Exception ex) {
                log.error("Add job error => shutdown job error",ex);
            }
            JsonResponse.notOk(e.getMessage());
        }
        return JsonResponse.ok();
    }

    @RequestMapping(value = "/exist",method = RequestMethod.POST)
    public JsonResponse existJob(@RequestParam final String jobName){
        try {
            JobConfig existJob = jobService.getJobByName(jobName);
            if(existJob != null){
                return JsonResponse.ok();
            }
            return JsonResponse.notOk("");
        } catch (Exception e) {
            log.error("update job fail: ",e);
            return JsonResponse.ok();
        }
    }

    @RequestMapping(value = "/update",method = RequestMethod.PUT)
    public JsonResponse updateJob(@RequestBody final JobConfig config){
        try {
            //判断cron和dependency
            if(Strings.isNullOrEmpty(config.getDependency()) && Strings.isNullOrEmpty(config.getCron())){
                return JsonResponse.notOk("Dependency or cron need to be configured,and only one be configured.");
            }else if(!Strings.isNullOrEmpty(config.getDependency()) && !Strings.isNullOrEmpty(config.getCron())){
                return JsonResponse.notOk("Dependency and cron only one can be configured");
            }
            //查依赖的作业是否存在
            if(!Strings.isNullOrEmpty(config.getDependency())){
                JobConfig existJob = jobService.getJobByName(config.getDependency());
                if(existJob == null){
                    return JsonResponse.notOk("Dependency job not exists!");
                }
            }
            ConfigurationService configurationService = new ConfigurationService(registryCenter,config.getJobName());
            configurationService.update(BeanConfigCopyUtil.copy(JobConfiguration.class,config,JobConfig.class));
            config.setUpdateTime(new Date());
            jobService.updateJob(config);
            return JsonResponse.ok();
        } catch (Exception e) {
            log.error("update job fail: ",e);
            return JsonResponse.notOk(e.getMessage());
        }
    }

    @RequestMapping(value = "/pause",method = RequestMethod.POST)
    public JsonResponse pauseJob(@RequestParam final String jobName){
        try {
            jobService.updateStatus(JobStatus.PAUSE.getFlag(),jobName,new Date());
            OperationService operationService = new OperationService(registryCenter,jobName);
            operationService.operation(InstanceOperation.PAUSE);
            return JsonResponse.ok();
        } catch (Exception e) {
            log.error("pause job fail: ",e);
            return JsonResponse.notOk(e.getMessage());
        }
    }

    @RequestMapping(value = "/resume",method = RequestMethod.POST)
    public JsonResponse resumeJob(@RequestParam final String jobName){
        try {
            jobService.updateStatus(JobStatus.STARTUP.getFlag(),jobName,new Date());
            OperationService operationService = new OperationService(registryCenter,jobName);
            operationService.operation(InstanceOperation.START);
            return JsonResponse.ok();
        } catch (Exception e) {
            log.error("Resume job fail: ",e);
            return JsonResponse.notOk(e.getMessage());
        }
    }

    @RequestMapping(value = "/trigger",method = RequestMethod.POST)
    public JsonResponse triggerJob(@RequestParam final String jobName){
        try {
            OperationService operationService = new OperationService(registryCenter,jobName);
            operationService.operation(InstanceOperation.TRIGGER);
            return JsonResponse.ok();
        } catch (Exception e) {
            log.error("Trigger job fail: ",e);
            return JsonResponse.notOk(e.getMessage());
        }
    }

    @RequestMapping(value = "/shutdown",method = RequestMethod.POST)
    public JsonResponse shutdownJob(@RequestParam final String jobName){
        try {
            //关闭一个作业，必须保证没有其它作业的依赖
            List<String> jobNames = jobService.getJobsByDep(jobName);
            if(jobNames !=null && !jobNames.isEmpty()){
                return JsonResponse.notOk(String.format("Job '%s' can not be deleted,because '%s' depend on it",jobName,Joiner.on(",").join(jobNames)));
            }
            OperationService operationService = new OperationService(registryCenter,jobName);
            operationService.operation(InstanceOperation.SHUTDOWN);
            jobService.deleteJob(jobName);
            return JsonResponse.ok();
        } catch (Exception e) {
            log.error("Shutdown job fail: ",e);
            return JsonResponse.notOk(e.getMessage());
        }
    }

    @RequestMapping(value = "/get_job_page",method = RequestMethod.GET)
    public JsonResponse getJobsByPage(@RequestParam String jobName,@RequestParam int page, @RequestParam int pageSize){
        try {
            int jobCnt = jobService.getJobCnt(jobName);
            PageParam pageParam = new PageParam(page,pageSize,jobCnt);
            List<BriefJobConfig> jobs = jobService.getJobByPage(jobName,pageParam.getStart(),pageSize);
            pageParam.setData(jobs);
            return JsonResponse.ok(pageParam);
        } catch (Exception e) {
            log.error("Get job by page fail: ",e);
            return JsonResponse.notOk(e.getMessage());
        }
    }

    @RequestMapping(value = "/get_job_by_id",method = RequestMethod.POST)
    public JsonResponse getJobById(@RequestParam int jobId){
        try {
            JobConfig jobConfig = jobService.getJobById(jobId);
            return JsonResponse.ok(jobConfig);
        } catch (Exception e) {
            log.error("Get job fail: ",e);
            return JsonResponse.notOk(e.getMessage());
        }
    }

    @RequestMapping(value = "/get_job_by_name",method = RequestMethod.POST)
    public JsonResponse getJobByName(@RequestParam String jobName){
        try {
            JobConfig jobConfig = jobService.getJobByName(jobName);
            return JsonResponse.ok(jobConfig);
        } catch (Exception e) {
            log.error("Get job fail: ",e);
            return JsonResponse.notOk(e.getMessage());
        }
    }

    @RequestMapping(value = "/disable",method = RequestMethod.POST)
    public JsonResponse disableExecuteNode(@RequestParam String jobName,@RequestParam String instanceId){
        try {
            ExecuteInstanceService instanceService = new ExecuteInstanceService(registryCenter,jobName);
            instanceService.updateNodeStatus(ServerStatus.DISABLED,instanceId);
            return JsonResponse.ok();
        } catch (Exception e) {
            log.error(String.format("Disable instance: %s fail: ",instanceId),e);
            return JsonResponse.notOk(e.getMessage());
        }
    }

    @RequestMapping(value = "/enable",method = RequestMethod.POST)
    public JsonResponse enableExecuteNode(@RequestParam String jobName,@RequestParam String instanceId){
        try {
            ExecuteInstanceService instanceService = new ExecuteInstanceService(registryCenter,jobName);
            instanceService.updateNodeStatus(ServerStatus.ENABLE,instanceId);
            return JsonResponse.ok();
        } catch (Exception e) {
            log.error(String.format("Enable instances: %s fail: ",instanceId),e);
            return JsonResponse.notOk(e.getMessage());
        }
    }

    @RequestMapping(value = "/detail/{jobName}",method = RequestMethod.POST)
    public JsonResponse getDetail(@PathVariable("jobName") String jobName){
        try {
            return JsonResponse.ok(detailService.getDetail(jobName));
        } catch (Exception e) {
            log.error("Get job detail fail",e);
            return JsonResponse.notOk(e.getMessage());
        }
    }
}
