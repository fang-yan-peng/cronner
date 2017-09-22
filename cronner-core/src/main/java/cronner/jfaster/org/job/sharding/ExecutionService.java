package cronner.jfaster.org.job.sharding;


import com.google.common.base.Joiner;
import cronner.jfaster.org.executor.ShardingContexts;
import cronner.jfaster.org.job.config.ConfigurationService;
import cronner.jfaster.org.job.schedule.JobRegistry;
import cronner.jfaster.org.job.storage.JobNodeStorage;
import cronner.jfaster.org.model.JobConfiguration;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 执行作业的服务.
 * @author fangyanpeng
 */
public final class ExecutionService {
    
    private final String jobName;
    
    private final JobNodeStorage jobNodeStorage;

    private final ConfigurationService configService;
    
    public ExecutionService(final CoordinatorRegistryCenter regCenter, final String jobName) {
        this.jobName = jobName;
        jobNodeStorage = new JobNodeStorage(regCenter, jobName);
        configService = new ConfigurationService(regCenter, jobName);
    }
        
    /**
     * 注册作业启动信息.
     * 
     * @param shardingContexts 分片上下文
     */
    public void registerJobBegin(final ShardingContexts shardingContexts) {
        JobRegistry.getInstance().setJobRunning(jobName, true);
        for(Map.Entry<Integer,Integer> itemTaskId : shardingContexts.getShardingItemTaskIds().entrySet()){
            String taskId = Joiner.on(",").join(shardingContexts.getTaskId(),itemTaskId.getValue());
            jobNodeStorage.updateJobNode(ShardingNode.getTaskNode(itemTaskId.getKey()),taskId);
        }
        if (!configService.load(true).isMonitorExecution()) {
            return;
        }
        for (int each : shardingContexts.getShardingItemParameters().keySet()) {
            jobNodeStorage.fillEphemeralJobNode(ShardingNode.getRunningNode(each), "");
        }
    }
    
    /**
     * 注册作业完成信息.
     * 
     * @param shardingContexts 分片上下文
     */
    public void registerJobCompleted(final ShardingContexts shardingContexts) {
        JobRegistry.getInstance().setJobRunning(jobName, false);
        for(int each : shardingContexts.getShardingItemTaskIds().keySet()){
            jobNodeStorage.updateJobNode(ShardingNode.getTaskNode(each),"");
        }
        if (!configService.load(true).isMonitorExecution()) {
            return;
        }
        for (int each : shardingContexts.getShardingItemParameters().keySet()) {
            jobNodeStorage.removeJobNodeIfExisted(ShardingNode.getRunningNode(each));
        }
    }
    
    /**
     * 清除全部分片的运行状态.
     */
    public void clearAllRunningInfo() {
        clearRunningInfo(getAllItems());
    }
    
    /**
     * 清除分配分片项的运行状态.
     * 
     * @param items 需要清理的分片项列表
     */
    public void clearRunningInfo(final List<Integer> items) {
        for (int each : items) {
            jobNodeStorage.removeJobNodeIfExisted(ShardingNode.getRunningNode(each));
        }
    }
    
    /**
     * 判断分片项中是否还有执行中的作业.
     *
     * @param items 需要判断的分片项列表
     * @return 分片项中是否还有执行中的作业
     */
    public boolean hasRunningItems(final Collection<Integer> items) {
        JobConfiguration jobConfig = configService.load(true);
        if (null == jobConfig || !jobConfig.isMonitorExecution()) {
            return false;
        }
        for (int each : items) {
            if (jobNodeStorage.isJobNodeExisted(ShardingNode.getRunningNode(each))) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 判断是否还有执行中的作业.
     *
     * @return 是否还有执行中的作业
     */
    public boolean hasRunningItems() {
        return hasRunningItems(getAllItems());
    }
    
    private List<Integer> getAllItems() {
        int shardingTotalCount = configService.load(true).getShardingTotalCount();
        List<Integer> result = new ArrayList<>(shardingTotalCount);
        for (int i = 0; i < shardingTotalCount; i++) {
            result.add(i);
        }
        return result;
    }
    
    /**
     * 如果当前分片项仍在运行则设置任务被错过执行的标记.
     * 
     * @param items 需要设置错过执行的任务分片项
     * @return 是否错过本次执行
     */
    public boolean misfireIfHasRunningItems(final Collection<Integer> items) {
        if (!hasRunningItems(items)) {
            return false;
        }
        setMisfire(items);
        return true;
    }
    
    /**
     * 设置任务被错过执行的标记.
     *
     * @param items 需要设置错过执行的任务分片项
     */
    public void setMisfire(final Collection<Integer> items) {
        for (int each : items) {
            if(jobNodeStorage.isJobNodeExisted(ShardingNode.getItemNode(each))){
                jobNodeStorage.createJobNodeIfNeeded(ShardingNode.getMisfireNode(each));
            }
        }
    }
    
    /**
     * 获取标记被错过执行的任务分片项.
     * 
     * @param items 需要获取标记被错过执行的任务分片项
     * @return 标记被错过执行的任务分片项
     */
    public List<Integer> getMisfiredJobItems(final Collection<Integer> items) {
        List<Integer> result = new ArrayList<>(items.size());
        for (int each : items) {
            if (jobNodeStorage.isJobNodeExisted(ShardingNode.getMisfireNode(each))) {
                result.add(each);
            }
        }
        return result;
    }
    
    /**
     * 清除任务被错过执行的标记.
     * 
     * @param items 需要清除错过执行的任务分片项
     */
    public void clearMisfire(final Collection<Integer> items) {
        for (int each : items) {
            jobNodeStorage.removeJobNodeIfExisted(ShardingNode.getMisfireNode(each));
        }
    }
    
    /**
     * 获取禁用的任务分片项.
     *
     * @param items 需要获取禁用的任务分片项
     * @return 禁用的任务分片项
     */
    public List<Integer> getDisabledItems(final List<Integer> items) {
        List<Integer> result = new ArrayList<>(items.size());
        for (int each : items) {
            if (jobNodeStorage.isJobNodeExisted(ShardingNode.getDisabledNode(each))) {
                result.add(each);
            }
        }
        return result;
    }
}
