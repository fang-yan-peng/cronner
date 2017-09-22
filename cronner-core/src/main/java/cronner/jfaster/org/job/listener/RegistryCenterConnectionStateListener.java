package cronner.jfaster.org.job.listener;

import cronner.jfaster.org.job.schedule.JobRegistry;
import cronner.jfaster.org.job.schedule.JobScheduleController;
import cronner.jfaster.org.job.server.ServerService;
import cronner.jfaster.org.job.sharding.ExecutionService;
import cronner.jfaster.org.job.sharding.ShardingService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;

/**
 * 注册中心连接状态监听器.
 * @author fangyanpeng
 */
public final class RegistryCenterConnectionStateListener implements ConnectionStateListener {
    
    private final String jobName;
    
    private final ServerService serverService;

    private final ShardingService shardingService;
    
    private final ExecutionService executionService;
    
    public RegistryCenterConnectionStateListener(ServerService serverService,ShardingService shardingService,ExecutionService executionService, final String jobName) {
        this.jobName = jobName;
        this.serverService = serverService;
        this.shardingService = shardingService;
        this.executionService = executionService;
    }
    
    @Override
    public void stateChanged(final CuratorFramework client, final ConnectionState newState) {
        if (JobRegistry.getInstance().isShutdown(jobName)) {
            return;
        }
        JobScheduleController jobScheduleController = JobRegistry.getInstance().getJobScheduleController(jobName);
        if (ConnectionState.SUSPENDED == newState || ConnectionState.LOST == newState) {
            jobScheduleController.pauseJob();
        } else if (ConnectionState.RECONNECTED == newState) {
            serverService.persistOnline(true);
            executionService.clearRunningInfo(shardingService.getShardingItems());
            jobScheduleController.resumeJob();
        }
    }
}
