package cronner.jfaster.org.job.guarantee;

import cronner.jfaster.org.executor.JobCompleteHandler;
import cronner.jfaster.org.job.listener.AbstractJobListener;
import cronner.jfaster.org.job.listener.AbstractListenerManager;
import cronner.jfaster.org.job.storage.JobNodeStorage;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent.Type;

/**
 * 保证分布式任务全部开始和结束状态监听管理器.
 * @author fangyanpeng
 */
public final class GuaranteeListenerManager extends AbstractListenerManager {
    
    private final GuaranteeNode guaranteeNode;

    private final String jobName;

    private final JobCompleteHandler handler;

    public GuaranteeListenerManager(JobNodeStorage jobNodeStorage, final String jobName, final JobCompleteHandler handler) {
        super(jobNodeStorage);
        this.jobName = jobName;
        this.handler = handler;
        this.guaranteeNode = new GuaranteeNode(jobName);
    }
    
    @Override
    public void start() {
        addDataListener(new CompletedNodeRemovedJobListener());
    }

    
    class CompletedNodeRemovedJobListener extends AbstractJobListener {
        
        @Override
        protected void dataChanged(final String path, final Type eventType, final String data) {
            if (Type.NODE_REMOVED == eventType && guaranteeNode.isCompletedRootNode(path)) {
                //作业运行完成
                handler.complete(jobName,jobNodeStorage.isJobNodeExisted(guaranteeNode.COMPLETED_FAIL_FLAG));
            }
        }
    }
}
