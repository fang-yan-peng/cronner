package cronner.jfaster.org.job.listener;

import com.google.common.base.Charsets;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

/**
 * 作业注册中心的监听器.
 * @author fangyanpeng
 * 
 */
public abstract class AbstractJobListener implements TreeCacheListener {
    
    @Override
    public final void childEvent(final CuratorFramework client, final TreeCacheEvent event) throws Exception {
        ChildData childData = event.getData();
        if (null == childData) {
            return;
        }
        String path = childData.getPath();
        if (path.isEmpty()) {
            return;
        }
        dataChanged(path, event.getType(), null == childData.getData() ? "" : new String(childData.getData(), Charsets.UTF_8));
    }
    
    protected abstract void dataChanged(final String path, final Type eventType, final String data);
}
