package cronner.jfaster.org.job.strategy.impl;


import cronner.jfaster.org.job.strategy.JobInstance;
import cronner.jfaster.org.job.strategy.JobShardingStrategy;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 根据作业名的哈希值奇偶数决定IP升降序算法的分片策略.
 * 
 * <p>
 * 作业名的哈希值为奇数则IP升序.
 * 作业名的哈希值为偶数则IP降序.
 * 用于不同的作业平均分配负载至不同的服务器.
 * 如: 
 * 1. 如果有3台服务器, 分成2片, 作业名称的哈希值为奇数, 则每台服务器分到的分片是: 1=[0], 2=[1], 3=[].
 * 2. 如果有3台服务器, 分成2片, 作业名称的哈希值为偶数, 则每台服务器分到的分片是: 3=[0], 2=[1], 1=[].
 * </p>
 * 
 */
public final class OdevitySortByNameJobShardingStrategy implements JobShardingStrategy {
    
    private AverageAllocationJobShardingStrategy averageAllocationJobShardingStrategy = new AverageAllocationJobShardingStrategy();
    
    @Override
    public Map<JobInstance, List<Integer>> sharding(final List<JobInstance> jobInstances, final String jobName, final int shardingTotalCount) {
        long jobNameHash = jobName.hashCode();
        if (0 == jobNameHash % 2) {
            Collections.reverse(jobInstances);
        }
        return averageAllocationJobShardingStrategy.sharding(jobInstances, jobName, shardingTotalCount);
    }
}
