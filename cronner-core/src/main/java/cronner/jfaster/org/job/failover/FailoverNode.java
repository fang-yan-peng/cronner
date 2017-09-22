package cronner.jfaster.org.job.failover;


import cronner.jfaster.org.job.election.LeaderNode;
import cronner.jfaster.org.job.sharding.ShardingNode;
import cronner.jfaster.org.job.storage.JobNodePath;

/**
 * 失效转移节点路径.
 * @author fangyanpeng
 */
public final class FailoverNode {
    
    static final String FAILOVER = "failover";
    
    static final String LEADER_ROOT = LeaderNode.ROOT + "/" + FAILOVER;
    
    static final String ITEMS_ROOT = LEADER_ROOT + "/items";
    
    static final String ITEMS = ITEMS_ROOT + "/%s";
    
    static final String LATCH = LEADER_ROOT + "/latch";
    
    private static final String EXECUTION_FAILOVER = ShardingNode.ROOT + "/%s/" + FAILOVER;
    
    private final JobNodePath jobNodePath;
    
    public FailoverNode(final String jobName) {
        jobNodePath = new JobNodePath(jobName);
    }
    
    static String getItemsNode(final int item) {
        return String.format(ITEMS, item);
    }
    
    static String getExecutionFailoverNode(final int item) {
        return String.format(EXECUTION_FAILOVER, item);
    }
    
    /**
     * 根据失效转移执行路径获取分片项.
     * 
     * @param path 失效转移执行路径
     * @return 分片项, 不是失效转移执行路径获则返回null
     */
    public Integer getItemByExecutionFailoverPath(final String path) {
        if (!isFailoverPath(path)) {
            return null;
        }
        return Integer.parseInt(path.substring(jobNodePath.getFullPath(ShardingNode.ROOT).length() + 1, path.lastIndexOf(FailoverNode.FAILOVER) - 1));
    }
    
    private boolean isFailoverPath(final String path) {
        return path.startsWith(jobNodePath.getFullPath(ShardingNode.ROOT)) && path.endsWith(FailoverNode.FAILOVER);
    }
}
