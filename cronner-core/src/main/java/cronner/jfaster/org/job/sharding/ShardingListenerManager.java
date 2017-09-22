package cronner.jfaster.org.job.sharding;

import cronner.jfaster.org.job.config.ConfigurationNode;
import cronner.jfaster.org.job.config.JobConfigurationGsonFactory;
import cronner.jfaster.org.job.instance.AbstractInstanceNode;
import cronner.jfaster.org.job.instance.ExecuteInstanceNode;
import cronner.jfaster.org.job.listener.AbstractJobListener;
import cronner.jfaster.org.job.listener.AbstractListenerManager;
import cronner.jfaster.org.job.schedule.JobRegistry;
import cronner.jfaster.org.job.storage.JobNodeStorage;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;

/**
 * 分片监听管理器.
 * @author fangyanpeng
 */
public final class ShardingListenerManager extends AbstractListenerManager {
    
    private final String jobName;
    
    private final ConfigurationNode configNode;
    
    private final AbstractInstanceNode instanceNode;

    private final ShardingService shardingService;
    
    public ShardingListenerManager(JobNodeStorage jobNodeStorage, String jobName, ShardingService shardingService) {
        super(jobNodeStorage);
        this.jobName = jobName;
        configNode = new ConfigurationNode(jobName);
        instanceNode = new ExecuteInstanceNode(jobName);
        this.shardingService = shardingService;
    }
    
    @Override
    public void start() {
        addDataListener(new ShardingTotalCountChangedJobListener());
        addDataListener(new ListenServersChangedJobListener());
    }
    
    class ShardingTotalCountChangedJobListener extends AbstractJobListener {
        
        @Override
        protected void dataChanged(final String path, final Type eventType, final String data) {
            if (configNode.isConfigPath(path) && 0 != JobRegistry.getInstance().getCurrentShardingTotalCount(jobName)) {
                int newShardingTotalCount = JobConfigurationGsonFactory.fromJson(data).getShardingTotalCount();
                if (newShardingTotalCount != JobRegistry.getInstance().getCurrentShardingTotalCount(jobName)) {
                    shardingService.setReshardingFlag();
                    JobRegistry.getInstance().setCurrentShardingTotalCount(jobName, newShardingTotalCount);
                }
            }
        }
    }
    
    class ListenServersChangedJobListener extends AbstractJobListener {
        
        @Override
        protected void dataChanged(final String path, final Type eventType, final String data) {
            if (!JobRegistry.getInstance().isShutdown(jobName) && isInstanceChange(eventType, path)) {
                shardingService.setReshardingFlag();
            }
        }
        
        private boolean isInstanceChange(final Type eventType, final String path) {
            return instanceNode.isInstancePath(path) && Type.NODE_UPDATED != eventType;
        }
        
    }
}
