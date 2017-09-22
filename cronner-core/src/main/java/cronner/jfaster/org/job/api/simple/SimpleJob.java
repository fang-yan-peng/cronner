package cronner.jfaster.org.job.api.simple;

import cronner.jfaster.org.job.api.CronnerJob;
import cronner.jfaster.org.job.api.ShardingContext;

/**
 * 简单分布式作业接口.
 * @author fangyanpeng
 */
public interface SimpleJob extends CronnerJob {
    
    /**
     * 执行作业.
     *
     * @param shardingContext 分片上下文
     */
    void execute(ShardingContext shardingContext);
}
