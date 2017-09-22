package cronner.jfaster.org.job.strategy;

import com.google.common.base.Strings;
import cronner.jfaster.org.exeception.JobConfigurationException;
import cronner.jfaster.org.job.strategy.impl.AverageAllocationJobShardingStrategy;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 作业分片策略工厂.
 * @author fangyanpeng
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JobShardingStrategyFactory {
    
    /**
     * 获取作业分片策略实例.
     * 
     * @param jobShardingStrategyClassName 作业分片策略类名
     * @return 作业分片策略实例
     */
    public static JobShardingStrategy getStrategy(final String jobShardingStrategyClassName) {
        if (Strings.isNullOrEmpty(jobShardingStrategyClassName)) {
            return new AverageAllocationJobShardingStrategy();
        }
        try {
            Class<?> jobShardingStrategyClass = Class.forName(jobShardingStrategyClassName);
            if (!JobShardingStrategy.class.isAssignableFrom(jobShardingStrategyClass)) {
                throw new JobConfigurationException("Class '%s' is not job strategy class", jobShardingStrategyClassName);
            }
            return (JobShardingStrategy) jobShardingStrategyClass.newInstance();
        } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            throw new JobConfigurationException("Sharding strategy class '%s' config error, message details are '%s'", jobShardingStrategyClassName, ex.getMessage());
        }
    }

    public static JobShardingStrategy getStrategy() {
        return new AverageAllocationJobShardingStrategy();
    }
}
