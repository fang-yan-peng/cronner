package cronner.jfaster.org.job.listener;

import cronner.jfaster.org.executor.JobCompleteHandler;
import cronner.jfaster.org.job.config.RescheduleListenerManager;
import cronner.jfaster.org.job.election.ElectionListenerManager;
import cronner.jfaster.org.job.election.LeaderService;
import cronner.jfaster.org.job.guarantee.GuaranteeListenerManager;
import cronner.jfaster.org.job.instance.OperationListenerManager;
import cronner.jfaster.org.job.operation.OperationService;
import cronner.jfaster.org.job.server.ServerService;
import cronner.jfaster.org.job.sharding.ExecutionService;
import cronner.jfaster.org.job.sharding.ShardingListenerManager;
import cronner.jfaster.org.job.sharding.ShardingService;
import cronner.jfaster.org.job.storage.JobNodeStorage;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;


/**
 * 作业注册中心的监听器管理者.
 * @author fangyanpeng
 */
public final class ScheduleListenerManager {
    
    private final JobNodeStorage jobNodeStorage;
    
    private final ElectionListenerManager electionListenerManager;

    private final OperationListenerManager triggerListenerManager;
    
    private final RescheduleListenerManager rescheduleListenerManager;

    private final RegistryCenterConnectionStateListener regCenterConnectionStateListener;

    private final GuaranteeListenerManager guaranteeListenerManager;

    private final ShardingListenerManager shardingListenerManager;
    
    public ScheduleListenerManager(final CoordinatorRegistryCenter regCenter, final String jobName, final JobCompleteHandler handler,final LeaderService leaderService,final ShardingService shardingService,final ServerService serverService,final ExecutionService executionService) {
        jobNodeStorage = new JobNodeStorage(regCenter, jobName);
        electionListenerManager = new ElectionListenerManager(jobNodeStorage, jobName,leaderService,serverService);
        triggerListenerManager = new OperationListenerManager(jobNodeStorage, jobName,leaderService,new OperationService(regCenter,jobName));
        guaranteeListenerManager = new GuaranteeListenerManager(jobNodeStorage,jobName,handler);
        rescheduleListenerManager = new RescheduleListenerManager(jobNodeStorage, jobName);
        shardingListenerManager = new ShardingListenerManager(jobNodeStorage, jobName,shardingService);
        regCenterConnectionStateListener = new RegistryCenterConnectionStateListener(serverService,shardingService,executionService, jobName);
    }
    
    /**
     * 开启所有监听器.
     */
    public void startAllListeners() {
        electionListenerManager.start();
        triggerListenerManager.start();
        rescheduleListenerManager.start();
        guaranteeListenerManager.start();
        shardingListenerManager.start();
        jobNodeStorage.addConnectionStateListener(regCenterConnectionStateListener);
    }
}
