package cronner.jfaster.org.job.schedule;

import cronner.jfaster.org.job.sharding.ExecutionService;
import cronner.jfaster.org.job.sharding.ShardingService;
import lombok.RequiredArgsConstructor;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;

/**
 * 作业触发监听器.
 * @author fangyanpeng
 */
@RequiredArgsConstructor
public final class JobTriggerListener extends TriggerListenerSupport {
    
    private final ExecutionService executionService;

    private final ShardingService shardingService;

    @Override
    public String getName() {
        return "JobTriggerListener";
    }
    
    @Override
    public void triggerMisfired(final Trigger trigger) {
        if (null != trigger.getPreviousFireTime()) {
            executionService.setMisfire(shardingService.getShardingItems());
        }
    }
}
