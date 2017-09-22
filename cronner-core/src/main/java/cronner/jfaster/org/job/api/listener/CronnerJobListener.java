package cronner.jfaster.org.job.api.listener;


import cronner.jfaster.org.executor.ShardingContexts;

/**
 * 弹性化分布式作业监听器接口.
 * @author fangyanpeng
 */
public interface CronnerJobListener {
    
    /**
     * 作业执行前的执行的方法.
     * 
     * @param shardingContexts 分片上下文
     */
    void beforeJobExecuted(final ShardingContexts shardingContexts);
    
    /**
     * 作业执行后的执行的方法.
     *
     * @param shardingContexts 分片上下文
     */
    void afterJobExecuted(final ShardingContexts shardingContexts);
}
