package cronner.jfaster.org.job.failover;

import cronner.jfaster.org.job.config.ConfigurationNode;
import cronner.jfaster.org.job.config.ConfigurationService;
import cronner.jfaster.org.job.config.JobConfigurationGsonFactory;
import cronner.jfaster.org.job.instance.AbstractInstanceNode;
import cronner.jfaster.org.job.instance.ExecuteInstanceNode;
import cronner.jfaster.org.job.listener.AbstractJobListener;
import cronner.jfaster.org.job.listener.AbstractListenerManager;
import cronner.jfaster.org.job.schedule.JobRegistry;
import cronner.jfaster.org.job.sharding.ShardingService;
import cronner.jfaster.org.job.storage.JobNodeStorage;
import cronner.jfaster.org.model.JobConfiguration;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;

import java.util.List;

/**
 * 失效转移监听管理器.
 *
 * @author fangyanpeng
 * 
 */
public final class FailoverListenerManager extends AbstractListenerManager {
    
    private final String jobName;
    
    private final ConfigurationService configService;
    
    private final ShardingService shardingService;
    
    private final FailoverService failoverService;
    
    private final ConfigurationNode configNode;
    
    private final AbstractInstanceNode instanceNode;
    
    public FailoverListenerManager(final JobNodeStorage  jobNodeStorage, final String jobName,ShardingService shardingService,FailoverService failoverService,ConfigurationService configurationService) {
        super(jobNodeStorage);
        this.jobName = jobName;
        this.configService = configurationService;
        this.shardingService = shardingService;
        this.failoverService = failoverService;
        configNode = new ConfigurationNode(jobName);
        instanceNode = new ExecuteInstanceNode(jobName);
    }
    
    @Override
    public void start() {
        addDataListener(new JobCrashedJobListener());
        addDataListener(new FailoverSettingsChangedJobListener());
    }
    
    private boolean isFailoverEnabled() {
        JobConfiguration jobConfig = configService.load(true);
        return null != jobConfig && jobConfig.isFailover();
    }
    
    class JobCrashedJobListener extends AbstractJobListener {
        
        @Override
        protected void dataChanged(final String path, final Type eventType, final String data) {
            if (isFailoverEnabled() && Type.NODE_REMOVED == eventType && instanceNode.isInstancePath(path)) {
                String jobInstanceId = path.substring(instanceNode.getInstanceFullPath().length() + 1);
                if (jobInstanceId.equals(JobRegistry.getInstance().getJobInstance(jobName).getJobInstanceId())) {
                    return;
                }
                List<Integer> failoverItems = failoverService.getFailoverItems(jobInstanceId);
                if (!failoverItems.isEmpty()) {
                    for (int each : failoverItems) {
                        failoverService.setCrashedFailoverFlag(each);
                        failoverService.failoverIfNecessary();
                    }
                } else {
                    for (int each : shardingService.getShardingItems(jobInstanceId)) {
                        failoverService.setCrashedFailoverFlag(each);
                        failoverService.failoverIfNecessary();
                    }
                }
            }
        }
    }
    
    class FailoverSettingsChangedJobListener extends AbstractJobListener {
        
        @Override
        protected void dataChanged(final String path, final Type eventType, final String data) {
            if (configNode.isConfigPath(path) && Type.NODE_UPDATED == eventType && !JobConfigurationGsonFactory.fromJson(data).isFailover()) {
                failoverService.removeFailoverInfo();
            }
        }
    }
}
