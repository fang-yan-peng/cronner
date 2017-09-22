package cronner.jfaster.org.executor;


import cronner.jfaster.org.event.type.JobExecutionEvent;
import cronner.jfaster.org.event.type.JobStatusTraceEvent;
import cronner.jfaster.org.model.JobConfiguration;

import java.util.Collection;

/**
 * 作业内部服务门面服务.
 *
 * @author fangyanpeng
 */
public interface JobFacade {
    
    /**
     * 读取作业配置.
     * 
     * @param fromCache 是否从缓存中读取
     * @return 作业配置
     */
    JobConfiguration loadJobRootConfiguration(boolean fromCache);

    /**
     * 如果需要失效转移, 则执行作业失效转移.
     */
    void failoverIfNecessary();
    
    /**
     * 注册作业启动信息.
     *
     * @param shardingContexts 分片上下文
     */
    void registerJobBegin(ShardingContexts shardingContexts);
    
    /**
     * 注册作业完成信息.
     *
     * @param shardingContexts 分片上下文
     */
    void registerJobCompleted(ShardingContexts shardingContexts);
    
    /**
     * 设置任务被错过执行的标记.
     *
     * @param shardingItems 需要设置错过执行的任务分片项
     * @return 是否满足misfire条件
     */
    boolean misfireIfRunning(Collection<Integer> shardingItems);
    
    /**
     * 清除任务被错过执行的标记.
     *
     * @param shardingItems 需要清除错过执行的任务分片项
     */
    void clearMisfire(Collection<Integer> shardingItems);
    
    /**
     * 判断作业是否需要执行错过的任务.
     * 
     * @param shardingItems 任务分片项集合
     * @return 作业是否需要执行错过的任务
     */
    boolean isExecuteMisfired(Collection<Integer> shardingItems);
    
    /**
     * 判断作业是否符合继续运行的条件.
     * 
     * <p>如果作业停止或需要重分片或非流式处理则作业将不会继续运行.</p>
     * 
     * @return 作业是否符合继续运行的条件
     */
    boolean isEligibleForJobRunning();
    
    /**判断是否需要重分片.
     *
     * @return 是否需要重分片
     */
    boolean isNeedSharding();
    
    /**
     * 作业执行前的执行的方法.
     *
     * @param shardingContexts 分片上下文
     */
    void beforeJobExecuted(ShardingContexts shardingContexts);
    
    /**
     * 作业执行后的执行的方法.
     *
     * @param shardingContexts 分片上下文
     */
    void afterJobExecuted(ShardingContexts shardingContexts);
    
    /**
     * 发布执行事件.
     *
     * @param jobExecutionEvent 作业执行事件
     */
    void postJobExecutionEvent(JobExecutionEvent jobExecutionEvent);
    
    /**
     * 发布作业状态追踪事件.
     *
     * @param jobStatusTraceEvent 作业状态追踪事件
     *
     */
    void postJobStatusTraceEvent(JobStatusTraceEvent jobStatusTraceEvent);

    /**
     * 获取作业名称.
     *
     * @return 作业名称
     */
    String getJobName();
}
