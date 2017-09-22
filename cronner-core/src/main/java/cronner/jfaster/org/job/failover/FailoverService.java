package cronner.jfaster.org.job.failover;

import com.google.common.base.*;
import com.google.common.base.Optional;
import cronner.jfaster.org.executor.ShardingContexts;
import cronner.jfaster.org.executor.store.JobExecutorRegistry;
import cronner.jfaster.org.job.config.ConfigurationService;
import cronner.jfaster.org.job.schedule.JobRegistry;
import cronner.jfaster.org.job.sharding.ShardingNode;
import cronner.jfaster.org.job.sharding.ShardingService;
import cronner.jfaster.org.job.storage.JobNodeStorage;
import cronner.jfaster.org.job.storage.LeaderExecutionCallback;
import cronner.jfaster.org.model.JobConfiguration;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;
import cronner.jfaster.org.util.executor.ExecuteThreadService;
import cronner.jfaster.org.util.shard.ShardingItemParameters;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 作业失效转移服务.
 * @author fangyanpeng
 */
@Slf4j
public final class FailoverService {
    
    private final String jobName;
    
    private final JobNodeStorage jobNodeStorage;
    
    private final ShardingService shardingService;

    private final ConfigurationService configService;
    
    public FailoverService(final CoordinatorRegistryCenter regCenter, final String jobName) {
        this.jobName = jobName;
        jobNodeStorage = new JobNodeStorage(regCenter, jobName);
        shardingService = new ShardingService(regCenter, jobName);
        configService = new ConfigurationService(regCenter,jobName);
    }
    
    /**
     * 设置失效的分片项标记.
     * 
     * @param item 崩溃的作业项
     */
    public void setCrashedFailoverFlag(final int item) {
        if (!isFailoverAssigned(item)) {
            jobNodeStorage.createJobNodeIfNeeded(FailoverNode.getItemsNode(item));
        }
    }
    
    private boolean isFailoverAssigned(final Integer item) {
        return jobNodeStorage.isJobNodeExisted(FailoverNode.getExecutionFailoverNode(item));
    }
    
    /**
     * 如果需要失效转移, 则执行作业失效转移.
     */
    public void failoverIfNecessary() {
        if (needFailover()) {
            jobNodeStorage.executeInLeader(FailoverNode.LATCH, new FailoverLeaderExecutionCallback());
        }
    }
    
    private boolean needFailover() {
        return jobNodeStorage.isJobNodeExisted(FailoverNode.ITEMS_ROOT) && !jobNodeStorage.getJobNodeChildrenKeys(FailoverNode.ITEMS_ROOT).isEmpty()
                && !JobRegistry.getInstance().isJobRunning(jobName);
    }
    
    /**
     * 更新执行完毕失效转移的分片项状态.
     * 
     * @param items 执行完毕失效转移的分片项集合
     */
    public void updateFailoverComplete(final Collection<Integer> items) {
        for (int each : items) {
            jobNodeStorage.removeJobNodeIfExisted(FailoverNode.getExecutionFailoverNode(each));
        }
    }
    
    /**
     * 获取作业服务器的失效转移分片项集合.
     *
     * @param jobInstanceId 作业运行实例主键
     * @return 作业失效转移的分片项集合
     */
    public List<Integer> getFailoverItems(final String jobInstanceId) {
        List<String> items = jobNodeStorage.getJobNodeChildrenKeys(ShardingNode.ROOT);
        List<Integer> result = new ArrayList<>(items.size());
        for (String each : items) {
            int item = Integer.parseInt(each);
            String node = FailoverNode.getExecutionFailoverNode(item);
            if (jobNodeStorage.isJobNodeExisted(node) && jobInstanceId.equals(jobNodeStorage.getJobNodeDataDirectly(node))) {
                result.add(item);
            }
        }
        Collections.sort(result);
        return result;
    }
    
    /**
     * 获取运行在本作业服务器的失效转移分片项集合.
     * 
     * @return 运行在本作业服务器的失效转移分片项集合
     */
    public List<Integer> getLocalFailoverItems() {
        if (JobRegistry.getInstance().isShutdown(jobName)) {
            return Collections.emptyList();
        }
        return getFailoverItems(JobRegistry.getInstance().getJobInstance(jobName).getJobInstanceId());
    }
    
    /**
     * 获取运行在本作业服务器的被失效转移的序列号.
     * 
     * @return 运行在本作业服务器的被失效转移的序列号
     */
    public List<Integer> getLocalTakeOffItems() {
        List<Integer> shardingItems = shardingService.getLocalShardingItems();
        List<Integer> result = new ArrayList<>(shardingItems.size());
        for (int each : shardingItems) {
            if (jobNodeStorage.isJobNodeExisted(FailoverNode.getExecutionFailoverNode(each))) {
                result.add(each);
            }
        }
        return result;
    }
    
    /**
     * 删除作业失效转移信息.
     */
    public void removeFailoverInfo() {
        for (String each : jobNodeStorage.getJobNodeChildrenKeys(ShardingNode.ROOT)) {
            jobNodeStorage.removeJobNodeIfExisted(FailoverNode.getExecutionFailoverNode(Integer.parseInt(each)));
        }
    }
    
    class FailoverLeaderExecutionCallback implements LeaderExecutionCallback {
        
        @Override
        public void execute() {
            if (!needFailover()) {
                return;
            }
            int crashedItem = Integer.parseInt(jobNodeStorage.getJobNodeChildrenKeys(FailoverNode.ITEMS_ROOT).get(0));
            log.debug("Failover job '{}' begin, crashed item '{}'", jobName, crashedItem);
            jobNodeStorage.fillEphemeralJobNode(FailoverNode.getExecutionFailoverNode(crashedItem), JobRegistry.getInstance().getJobInstance(jobName).getJobInstanceId());
            jobNodeStorage.removeJobNodeIfExisted(FailoverNode.getItemsNode(crashedItem));
            final Optional<ShardingContexts> crashedContexts= getFailoverShardingContexts(crashedItem);
            if(crashedContexts.isPresent()){
                ExecuteThreadService.sumbmit(new Runnable() {
                    @Override
                    public void run() {
                        JobExecutorRegistry.getInstance().getJobExecutor(jobName).execute(crashedContexts.get());
                    }
                });
            }
        }

        private Optional<ShardingContexts> getFailoverShardingContexts(int crashedItem){
            String taskId;
            String taskIdNode = ShardingNode.getTaskNode(crashedItem);
            if(!jobNodeStorage.isJobNodeExisted(taskIdNode) || Strings.isNullOrEmpty(taskId = jobNodeStorage.getJobNodeData(taskIdNode))){
                return Optional.absent();
            }
            JobConfiguration jobConfiguration = configService.load(true);
            Iterator<String> iterator = Splitter.on(",").split(taskId).iterator();
            Map<Integer,String> shardingParameterMap = new ShardingItemParameters(jobConfiguration.getShardingParameter()).getMap();
            Map<Integer,String> crashedShardingParameterMap = new HashMap<>(1);
            crashedShardingParameterMap.put(crashedItem,shardingParameterMap.get(crashedItem));
            ShardingContexts contexts = new ShardingContexts(jobName,jobConfiguration.getShardingTotalCount(),jobConfiguration.getJobParameter(),crashedShardingParameterMap);
            contexts.setTaskId(Integer.parseInt(iterator.next()));
            contexts.addItemTaskId(crashedItem,Integer.parseInt(iterator.next()));
            contexts.setFailover(true);
            contexts.setAllowSendJobEvent(jobConfiguration.isAllowSendJobEvent());
            contexts.setStreamingProcess(jobConfiguration.isStreamingProcess());
            return Optional.of(contexts);
        }
    }
}
