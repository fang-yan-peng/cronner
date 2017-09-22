package cronner.jfaster.org.event.support;

import com.google.common.base.Optional;
import cronner.jfaster.org.event.JobEvent;
import cronner.jfaster.org.event.JobEventListener;
import cronner.jfaster.org.event.type.JobExecutionEvent;
import cronner.jfaster.org.event.type.JobStatusTraceEvent;
import cronner.jfaster.org.job.schedule.ScheduleService;
import cronner.jfaster.org.util.http.Http;
import cronner.jfaster.org.util.http.HttpMethod;
import cronner.jfaster.org.util.json.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

import static cronner.jfaster.org.constants.CronnerConstant.TASK_EXECUTE_EVENT_URL;
import static cronner.jfaster.org.constants.CronnerConstant.TASK_STATUS_EVENT_URL;


/**
 * 运行痕迹事件数据库监听器.
 *
 * @author fangyanpeng
 *
 */
@Slf4j
@RequiredArgsConstructor
public final class JobExecuteEventListener implements JobEventListener {

    private final ScheduleService scheduleService;

    private final int retry = 3;

    @Override
    public void listen(final JobExecutionEvent executionEvent) {
        sendEvent(executionEvent,TASK_EXECUTE_EVENT_URL);
    }
    
    @Override
    public void listen(final JobStatusTraceEvent jobStatusTraceEvent) {
        sendEvent(jobStatusTraceEvent,TASK_STATUS_EVENT_URL);
    }

    /**
     *
     * 发送作业执行信息给调度节点，如果尝试3次发送失败后，放弃发送，有可能所有的调度节点全都down机
     *
     * @param event
     * @param format
     */
    private void sendEvent(JobEvent event,String format){
        int i = 1;
        while (i <= retry){
            try {
                Optional<String> schedulerHost = scheduleService.getSchedulerHost();
                if(!schedulerHost.isPresent()){
                    log.info(event.getJobName() + " None scheduler node is alive");
                    return;
                }
                Http http = new Http(String.format(format,schedulerHost.get()));
                http.method(HttpMethod.PUT);
                http.contentType(Http.JSON_CONNTENT_TYPE);
                http.body(GsonFactory.getGson().toJson(event));
                http.request();
                return;
            } catch (Exception e) {
                ++i;
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException interruptExp) {
                }
                if(i == retry){
                    log.error(event.getJobName() + " Retry " + retry + "times, fail to send event,please make sure some schedulers are alive ",e);
                }else {
                    log.warn(event.getJobName() + " send event to scheduler fail,scheduler may have down,try to another scheduler");
                }
            }
        }

    }

}
