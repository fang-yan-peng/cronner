package cronner.jfaster.org.job.api;

import cronner.jfaster.org.executor.ShardingContexts;
import lombok.Getter;
import lombok.ToString;

/**
 * 分片上下文.
 * @author fangyanpeng
 */
@Getter
@ToString
public final class ShardingContext {
    
    /**
     * 作业名称.
     */
    private final String jobName;
    
    /**
     * 作业任务ID.
     */
    private final int taskId;
    
    /**
     * 分片总数.
     */
    private final int shardingTotalCount;
    
    /**
     * 作业自定义参数.
     * 可以配置多个相同的作业, 但是用不同的参数作为不同的调度实例.
     */
    private final String jobParameter;
    
    /**
     * 分配于本作业实例的分片项.
     */
    private final int shardingItem;

    private boolean streamingProcess;
    
    /**
     * 分配于本作业实例的分片参数.
     */
    private final String shardingParameter;
    
    public ShardingContext(final ShardingContexts shardingContexts, final int shardingItem) {
        jobName = shardingContexts.getJobName();
        taskId = shardingContexts.getTaskId();
        shardingTotalCount = shardingContexts.getShardingTotalCount();
        jobParameter = shardingContexts.getJobParameter();
        this.shardingItem = shardingItem;
        streamingProcess = shardingContexts.isStreamingProcess();
        shardingParameter = shardingContexts.getShardingItemParameters().get(shardingItem);
    }
}
