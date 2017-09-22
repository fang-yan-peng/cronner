package cronner.jfaster.org.job.listener;

import cronner.jfaster.org.job.storage.JobNodeStorage;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

/**
 * 作业注册中心的监听器管理者的抽象类.
 *
 * @author fangyanpeng
 * 
 */
public abstract class AbstractListenerManager {
    
    private final JobNodeStorage jobNodeStorage;
    
    protected AbstractListenerManager(JobNodeStorage jobNodeStorage) {
        this.jobNodeStorage = jobNodeStorage;
    }

    /**
     * 开启监听器.
     */
    public abstract void start();
    
    protected void addDataListener(final TreeCacheListener listener) {
        jobNodeStorage.addDataListener(listener);
    }
}
