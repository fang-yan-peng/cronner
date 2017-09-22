package cronner.jfaster.org.job.sharding;

import cronner.jfaster.org.job.config.ConfigurationNode;
import cronner.jfaster.org.job.config.JobConfigurationGsonFactory;
import cronner.jfaster.org.job.listener.AbstractJobListener;
import cronner.jfaster.org.job.listener.AbstractListenerManager;
import cronner.jfaster.org.job.storage.JobNodeStorage;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;

/**
 * 幂等性监听管理器.
 * @author fangyanpeng
 */
public final class MonitorExecutionListenerManager extends AbstractListenerManager {
    
    private final ExecutionService executionService;
    
    private final ConfigurationNode configNode;
    
    public MonitorExecutionListenerManager(JobNodeStorage jobNodeStorage, String jobName ,final ExecutionService executionService) {
        super(jobNodeStorage);
        this.executionService = executionService;
        configNode = new ConfigurationNode(jobName);
    }
    
    @Override
    public void start() {
        addDataListener(new MonitorExecutionSettingsChangedJobListener());
    }
    
    class MonitorExecutionSettingsChangedJobListener extends AbstractJobListener {
        
        @Override
        protected void dataChanged(final String path, final Type eventType, final String data) {
            if (configNode.isConfigPath(path) && Type.NODE_UPDATED == eventType && !JobConfigurationGsonFactory.fromJson(data).isMonitorExecution()) {
                executionService.clearAllRunningInfo();
            }
        }
    }
}
