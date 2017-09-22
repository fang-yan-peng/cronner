package cronner.jfaster.org.event;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import cronner.jfaster.org.event.support.JobExecuteEventListener;
import cronner.jfaster.org.job.schedule.ScheduleService;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;
import cronner.jfaster.org.util.ExecutorServiceObject;
import lombok.extern.slf4j.Slf4j;

/**
 * 运行痕迹事件总线.
 * @author fangyanpeng
 */
@Slf4j
public final class JobEventBus {
    

    private final ExecutorServiceObject executorServiceObject;
    
    private final EventBus eventBus;
    
    private boolean isRegistered;

    private CoordinatorRegistryCenter registryCenter;

    private String jobName;

    public JobEventBus(){

        executorServiceObject = null;

        eventBus = null;

    }
    
    public JobEventBus(CoordinatorRegistryCenter registryCenter,String jobName) {
        this.jobName = jobName;
        this.registryCenter = registryCenter;
        executorServiceObject = new ExecutorServiceObject("job-event", Runtime.getRuntime().availableProcessors() * 2);
        eventBus = new AsyncEventBus(executorServiceObject.createExecutorService());
        register();
    }
    
    private void register() {
        try {
            eventBus.register(new JobExecuteEventListener(new ScheduleService(registryCenter,jobName)));
            isRegistered = true;
        } catch (final Exception ex) {
            log.error("Cronner job: create jobExecuteEventListener failure, error is: ", ex);
        }
    }
    
    /**
     * 发布事件.
     *
     * @param event 作业事件
     */
    public void post(final JobEvent event) {
        if (isRegistered && !executorServiceObject.isShutdown()) {
            eventBus.post(event);
        }
    }
}
