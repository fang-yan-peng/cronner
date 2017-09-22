package cronner.jfaster.org.job.api.dataflow;

import cronner.jfaster.org.job.api.CronnerJob;
import cronner.jfaster.org.job.api.ShardingContext;

import java.util.List;

/**
 * 数据流分布式作业接口.
 * @author fangyanpeng
 * @param <T> 数据类型
 */
public interface DataflowJob<T> extends CronnerJob {
    
    /**
     * 获取待处理数据.
     *
     * @param shardingContext 分片上下文
     * @return 待处理的数据集合
     */
    List<T> fetchData(ShardingContext shardingContext);
    
    /**
     * 处理数据.
     *
     * @param shardingContext 分片上下文
     * @param data 待处理数据集合
     */
    void processData(ShardingContext shardingContext, List<T> data);
}
