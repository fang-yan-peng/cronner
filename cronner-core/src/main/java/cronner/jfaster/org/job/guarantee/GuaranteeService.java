package cronner.jfaster.org.job.guarantee;


import cronner.jfaster.org.job.config.ConfigurationService;
import cronner.jfaster.org.job.storage.JobNodeStorage;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;

import java.util.Collection;

/**
 * 保证分布式任务全部开始和结束状态的服务.
 * @author fangyanpeng
 */
public final class GuaranteeService {
    
    private final JobNodeStorage jobNodeStorage;
    
    private final ConfigurationService configService;
    
    public GuaranteeService(final CoordinatorRegistryCenter regCenter, final String jobName) {
        jobNodeStorage = new JobNodeStorage(regCenter, jobName);
        configService = new ConfigurationService(regCenter, jobName);
    }

    /**
     * 根据分片项注册任务完成运行.
     *
     * @param shardingItems 待注册的分片项
     */
    public void registerComplete(final Collection<Integer> shardingItems) {
        for (int each : shardingItems) {
            jobNodeStorage.createJobNodeIfNeeded(GuaranteeNode.getCompletedNode(each));
        }
    }
    
    /**
     * 判断是否所有的任务均执行完毕.
     *
     * @return 是否所有的任务均执行完毕
     */
    public boolean isAllCompleted() {
        return jobNodeStorage.isJobNodeExisted(GuaranteeNode.COMPLETED_ROOT)
                && configService.load(false).getShardingTotalCount() <= jobNodeStorage.getJobNodeChildrenKeys(GuaranteeNode.COMPLETED_ROOT).size();
    }
    
    /**
     * 清理所有任务启动信息.
     */
    public void clearAllCompletedInfo() {
        jobNodeStorage.removeJobNodeIfExisted(GuaranteeNode.COMPLETED_ROOT);
    }
}
