package cronner.jfaster.org.job.election;

import cronner.jfaster.org.job.listener.AbstractJobListener;
import cronner.jfaster.org.job.listener.AbstractListenerManager;
import cronner.jfaster.org.job.schedule.JobRegistry;
import cronner.jfaster.org.job.server.ServerNode;
import cronner.jfaster.org.job.server.ServerService;
import cronner.jfaster.org.job.server.ServerStatus;
import cronner.jfaster.org.job.storage.JobNodeStorage;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;

/**
 * 主节点选举监听管理器.
 * @author fangyanpeng
 */
public final class ElectionListenerManager extends AbstractListenerManager {
    
    private final String jobName;
    
    private final LeaderNode leaderNode;
    
    private final ServerNode serverNode;
    
    private final LeaderService leaderService;

    private final ServerService serverService;

    public ElectionListenerManager(JobNodeStorage jobNodeStorage, final String jobName, LeaderService leaderService, ServerService serverService) {
        super(jobNodeStorage);
        this.jobName = jobName;
        leaderNode = new LeaderNode(jobName);
        serverNode = new ServerNode(jobName);
        this.leaderService = leaderService;
        this.serverService = serverService;
    }
    
    @Override
    public void start() {
        addDataListener(new LeaderElectionJobListener());
        addDataListener(new LeaderAbdicationJobListener());
    }
    
    class LeaderElectionJobListener extends AbstractJobListener {
        
        @Override
        protected void dataChanged(final String path, final Type eventType, final String data) {
            if (!JobRegistry.getInstance().isShutdown(jobName) && (isActiveElection(path, data) || isPassiveElection(path, eventType))) {
                leaderService.electLeader();

            }
        }
        
        private boolean isActiveElection(final String path, final String data) {
            return !leaderService.hasLeader() && isLocalServerEnabled(path, data);
        }

        private boolean isPassiveElection(final String path, final Type eventType) {
            return isLeaderCrashed(path, eventType) && serverService.isAvailableServer(JobRegistry.getInstance().getJobInstance(jobName).getIp());
        }

        private boolean isLeaderCrashed(final String path, final Type eventType) {
            return leaderNode.isLeaderInstancePath(path) && Type.NODE_REMOVED == eventType;
        }
        
        private boolean isLocalServerEnabled(final String path, final String data) {
            return serverNode.isLocalServerPath(path) && !ServerStatus.DISABLED.name().equals(data);
        }
    }
    
    class LeaderAbdicationJobListener extends AbstractJobListener {
        
        @Override
        protected void dataChanged(final String path, final Type eventType, final String data) {
            if (leaderService.isLeader() && isLocalServerDisabled(path, data)) {
                leaderService.removeLeader();
            }
        }
        
        private boolean isLocalServerDisabled(final String path, final String data) {
            return serverNode.isLocalServerPath(path) && ServerStatus.DISABLED.name().equals(data);
        }
    }
}
