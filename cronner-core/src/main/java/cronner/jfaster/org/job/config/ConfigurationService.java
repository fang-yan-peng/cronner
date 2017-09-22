package cronner.jfaster.org.job.config;

import com.google.common.base.Optional;
import cronner.jfaster.org.job.storage.JobNodeStorage;
import cronner.jfaster.org.model.JobConfiguration;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;

/**
 * 弹性化分布式作业配置服务.
 * @author fangyanpeng
 */
public final class ConfigurationService {
    

    private final JobNodeStorage jobNodeStorage;
    
    public ConfigurationService(final CoordinatorRegistryCenter regCenter, final String jobName) {
        jobNodeStorage = new JobNodeStorage(regCenter, jobName);
    }
    
    /**
     * 读取作业配置.
     * 
     * @param fromCache 是否从缓存中读取
     * @return 作业配置
     */
    public JobConfiguration load(final boolean fromCache) {
        String result;
        if (fromCache) {
            result = jobNodeStorage.getJobNodeData(ConfigurationNode.ROOT);
            if (null == result) {
                result = jobNodeStorage.getJobNodeDataDirectly(ConfigurationNode.ROOT);
            }
        } else {
            result = jobNodeStorage.getJobNodeDataDirectly(ConfigurationNode.ROOT);
        }
        return JobConfigurationGsonFactory.fromJson(result);
    }
    
    /**
     * 持久化分布式作业配置信息.
     * 
     * @param jobConfig 作业配置
     */
    public void persist(final JobConfiguration jobConfig) {
        if (!jobNodeStorage.isJobNodeExisted(ConfigurationNode.ROOT)) {
            jobNodeStorage.replaceJobNode(ConfigurationNode.ROOT, JobConfigurationGsonFactory.toJson(jobConfig));
        }
    }

    /**
     *
     * 更新配置.
     *
     * @param jobConfig 作业配置
     */
    public void update(final JobConfiguration jobConfig) {
        if (jobNodeStorage.isJobNodeExisted(ConfigurationNode.ROOT)) {
            jobNodeStorage.replaceJobNode(ConfigurationNode.ROOT, JobConfigurationGsonFactory.toJson(jobConfig));
        }
    }


    
    private Optional<JobConfiguration> find() {
        if (!jobNodeStorage.isJobNodeExisted(ConfigurationNode.ROOT)) {
            return Optional.absent();
        }
        JobConfiguration result = JobConfigurationGsonFactory.fromJson(jobNodeStorage.getJobNodeDataDirectly(ConfigurationNode.ROOT));
        if (null == result) {
            // TODO 应该删除整个job node,并非仅仅删除config node
            jobNodeStorage.removeJobNodeIfExisted(ConfigurationNode.ROOT);
        }
        return Optional.fromNullable(result);
    }
}
