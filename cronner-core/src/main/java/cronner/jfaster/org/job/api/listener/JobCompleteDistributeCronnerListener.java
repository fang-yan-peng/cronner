package cronner.jfaster.org.job.api.listener;

import cronner.jfaster.org.executor.ShardingContexts;
import cronner.jfaster.org.job.guarantee.GuaranteeService;
import lombok.RequiredArgsConstructor;

/**
 * 在分布式作业中监听所有分片执行完成监听器.
 * @author fangyanpeng
 */
@RequiredArgsConstructor
public class JobCompleteDistributeCronnerListener implements CronnerJobListener {

    private final GuaranteeService guaranteeService;
    
    @Override
    public final void beforeJobExecuted(final ShardingContexts shardingContexts) {

    }
    
    @Override
    public final void afterJobExecuted(final ShardingContexts shardingContexts) {
        guaranteeService.registerComplete(shardingContexts.getShardingItemParameters().keySet());
        if (guaranteeService.isAllCompleted()) {
            guaranteeService.clearAllCompletedInfo();
            return;
        }
    }
}
