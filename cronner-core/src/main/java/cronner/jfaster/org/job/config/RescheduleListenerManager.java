package cronner.jfaster.org.job.config;

import cronner.jfaster.org.job.listener.AbstractJobListener;
import cronner.jfaster.org.job.listener.AbstractListenerManager;
import cronner.jfaster.org.job.schedule.JobRegistry;
import cronner.jfaster.org.job.storage.JobNodeStorage;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;

/**
 * 重调度监听管理器.
 * @author fangyanpeng
 */
public final class RescheduleListenerManager extends AbstractListenerManager {
    
    private final ConfigurationNode configNode;
    
    private final String jobName;
    
    public RescheduleListenerManager(final JobNodeStorage jobNodeStorage, final String jobName) {
        super(jobNodeStorage);
        this.jobName = jobName;
        configNode = new ConfigurationNode(jobName);
    }
    
    @Override
    public void start() {
        addDataListener(new CronSettingAndJobEventChangedJobListener());
    }
    
    class CronSettingAndJobEventChangedJobListener extends AbstractJobListener {
        
        @Override
        protected void dataChanged(final String path, final Type eventType, final String data) {
            if (configNode.isConfigPath(path) && Type.NODE_UPDATED == eventType && !JobRegistry.getInstance().isShutdown(jobName)) {
                JobRegistry.getInstance().getJobScheduleController(jobName).rescheduleJob(JobConfigurationGsonFactory.fromJson(data).getCron());
            }
        }
    }
}
