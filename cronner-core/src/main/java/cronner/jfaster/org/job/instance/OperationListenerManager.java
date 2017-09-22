package cronner.jfaster.org.job.instance;

import cronner.jfaster.org.job.election.LeaderService;
import cronner.jfaster.org.job.listener.AbstractJobListener;
import cronner.jfaster.org.job.listener.AbstractListenerManager;
import cronner.jfaster.org.job.operation.OperationService;
import cronner.jfaster.org.job.schedule.JobRegistry;
import cronner.jfaster.org.job.schedule.JobScheduleController;
import cronner.jfaster.org.job.storage.JobNodeStorage;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;

/**
 * 作业触发监听管理器.
 * @author fangyanpeng
 */
public final class OperationListenerManager extends AbstractListenerManager {
    
    private final String jobName;

    private final OperationService operationService;

    private final LeaderService leaderService;

    public OperationListenerManager(JobNodeStorage jobNodeStorage, final String jobName,final LeaderService leaderService, final OperationService operationService) {
        super(jobNodeStorage);
        this.jobName = jobName;
        this.operationService = operationService;
        this.leaderService = leaderService;

    }

    /**
     * 终止作业调度.
     */
    public void shutdownInstance() {
        if (leaderService.isLeader()) {
            leaderService.removeLeader();
        }
        JobRegistry.getInstance().shutdown(jobName);
    }
    
    @Override
    public void start() {
        addDataListener(new JobTriggerStatusJobListener());
    }
    
    class JobTriggerStatusJobListener extends AbstractJobListener {
        
        @Override
        protected void dataChanged(final String path, final Type eventType, final String data) {
            if (!(InstanceOperation.TRIGGER.name().equals(data)
                    || InstanceOperation.PAUSE.name().equals(data)
                    || InstanceOperation.START.name().equals(data)
                    || InstanceOperation.SHUTDOWN.name().equals(data)) || !operationService.isOperationPath(path) || Type.NODE_UPDATED != eventType) {
                return;
            }
            operationService.clearTriggerFlag();
            if (!JobRegistry.getInstance().isShutdown(jobName) && !JobRegistry.getInstance().isJobRunning(jobName)) {

                JobScheduleController controller = JobRegistry.getInstance().getJobScheduleController(jobName);

                if(InstanceOperation.TRIGGER.name().equals(data)){
                    if(!JobRegistry.getInstance().isJobRunning(jobName)) {
                        controller.triggerJob();
                    }
                }else if(InstanceOperation.PAUSE.name().equals(data)){
                    if(!controller.isPaused()) {
                        controller.pauseJob();
                    }
                }else if(InstanceOperation.START.name().equals(data)){
                    if(controller.isPaused()) {
                        JobRegistry.getInstance().getJobScheduleController(jobName).resumeJob();
                    }
                }else {
                    shutdownInstance();
                }
            }
        }
    }
}
