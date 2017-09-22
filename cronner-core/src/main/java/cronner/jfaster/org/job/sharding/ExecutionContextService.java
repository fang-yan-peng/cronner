package cronner.jfaster.org.job.sharding;

import cronner.jfaster.org.executor.ShardingContexts;
import cronner.jfaster.org.job.config.ConfigurationService;
import cronner.jfaster.org.job.storage.JobNodeStorage;
import cronner.jfaster.org.model.JobConfiguration;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;
import cronner.jfaster.org.util.shard.ShardingItemParameters;

import java.util.*;

/**
 * 作业运行时上下文服务.
 * @author fangyanpeng
 */
public final class ExecutionContextService {
    

    private final JobNodeStorage jobNodeStorage;
    
    private final ConfigurationService configService;
    
    public ExecutionContextService(final CoordinatorRegistryCenter regCenter, final String jobName) {
        jobNodeStorage = new JobNodeStorage(regCenter, jobName);
        configService = new ConfigurationService(regCenter, jobName);
    }
    
    /**
     * 获取当前作业服务器分片上下文.
     * 
     * @param shardingItems 分片项
     * @return 分片上下文
     */
    public ShardingContexts getJobShardingContext(final List<Integer> shardingItems) {
        JobConfiguration jobConfig = configService.load(false);
        removeRunningIfMonitorExecution(jobConfig.isMonitorExecution(), shardingItems);
        if (shardingItems.isEmpty()) {
            return new ShardingContexts(jobConfig.getJobName(), jobConfig.getShardingTotalCount(),
                    jobConfig.getJobParameter(), Collections.<Integer, String>emptyMap());
        }
        Map<Integer, String> shardingItemParameterMap = new ShardingItemParameters(jobConfig.getShardingParameter()).getMap();
        ShardingContexts contexts = new ShardingContexts(jobConfig.getJobName(), jobConfig.getShardingTotalCount(), jobConfig.getJobParameter(), getAssignedShardingItemParameterMap(shardingItems, shardingItemParameterMap));
        contexts.setAllowSendJobEvent(jobConfig.isAllowSendJobEvent());
        contexts.setStreamingProcess(jobConfig.isStreamingProcess());
        return contexts;
    }
    
    private void removeRunningIfMonitorExecution(final boolean monitorExecution, final List<Integer> shardingItems) {
        if (!monitorExecution) {
            return;
        }
        List<Integer> runningShardingItems = new ArrayList<>(shardingItems.size());
        for (int each : shardingItems) {
            if (isRunning(each)) {
                runningShardingItems.add(each);
            }
        }
        shardingItems.removeAll(runningShardingItems);
    }
    
    private boolean isRunning(final int shardingItem) {
        return jobNodeStorage.isJobNodeExisted(ShardingNode.getRunningNode(shardingItem));
    }
    
    private Map<Integer, String> getAssignedShardingItemParameterMap(final List<Integer> shardingItems, final Map<Integer, String> shardingItemParameterMap) {
        Map<Integer, String> result = new HashMap<>(shardingItemParameterMap.size(), 1);
        for (int each : shardingItems) {
            result.put(each, shardingItemParameterMap.get(each));
        }
        return result;
    }
}
