package cronner.jfaster.org.job.guarantee;

import com.google.common.base.Joiner;
import cronner.jfaster.org.job.storage.JobNodePath;

/**
 * 保证分布式任务全部开始和结束状态节点路径.
 * @author fangyanpeng
 */
public final class GuaranteeNode {
    
    static final String ROOT = "guarantee";
    

    static final String COMPLETED_ROOT = ROOT + "/completed";
    
    private final JobNodePath jobNodePath;
    
    GuaranteeNode(final String jobName) {
        jobNodePath = new JobNodePath(jobName);
    }

    static String getCompletedNode(final int shardingItem) {
        return Joiner.on("/").join(COMPLETED_ROOT, shardingItem);
    }

    boolean isCompletedRootNode(final String path) {
        return jobNodePath.getFullPath(COMPLETED_ROOT).equals(path);
    }
}
