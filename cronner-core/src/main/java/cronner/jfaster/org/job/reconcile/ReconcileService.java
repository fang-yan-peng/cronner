package cronner.jfaster.org.job.reconcile;

import com.google.common.util.concurrent.AbstractScheduledService;
import cronner.jfaster.org.job.config.ConfigurationService;
import cronner.jfaster.org.job.election.LeaderService;
import cronner.jfaster.org.job.sharding.ShardingService;
import cronner.jfaster.org.model.JobConfiguration;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 调解分布式作业不一致状态服务.
 * @author fangyanpeng
 */
@Slf4j
public final class ReconcileService extends AbstractScheduledService {
    
    private long lastReconcileTime;
    
    private final ConfigurationService configService;
    
    private final ShardingService shardingService;
    
    private final LeaderService leaderService;
    
    public ReconcileService(final CoordinatorRegistryCenter regCenter, final String jobName) {
        lastReconcileTime = System.currentTimeMillis();
        configService = new ConfigurationService(regCenter, jobName);
        shardingService = new ShardingService(regCenter, jobName);
        leaderService = new LeaderService(regCenter, jobName);
    }
    
    @Override
    protected void runOneIteration() throws Exception {
        JobConfiguration config = configService.load(true);
        int reconcileIntervalMinutes = null == config ? -1 : config.getReconcileIntervalMinutes();
        if (reconcileIntervalMinutes > 0 && (System.currentTimeMillis() - lastReconcileTime >= reconcileIntervalMinutes * 60 * 1000)) {
            lastReconcileTime = System.currentTimeMillis();
            if (leaderService.isLeaderUntilBlock() && !shardingService.isNeedSharding() && shardingService.hasShardingInfoInOfflineServers()) {
                log.warn("Cronner Job: job status node has inconsistent value,start reconciling...");
                shardingService.setReshardingFlag();
            }
        }
    }
    
    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0, 1, TimeUnit.MINUTES);
    }
}
