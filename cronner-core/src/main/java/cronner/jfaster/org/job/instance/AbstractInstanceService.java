package cronner.jfaster.org.job.instance;

import cronner.jfaster.org.job.server.ServerStatus;
import cronner.jfaster.org.job.storage.JobNodeStorage;
import cronner.jfaster.org.job.strategy.JobInstance;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;

import java.util.List;

/**
 * 作业运行实例服务.
 *
 * @author fangyanpeng
 */
public abstract class AbstractInstanceService {
    
    protected final JobNodeStorage jobNodeStorage;
    
    protected final AbstractInstanceNode instanceNode;
    

    public AbstractInstanceService(final CoordinatorRegistryCenter regCenter, final String jobName, final AbstractInstanceNode instanceNode) {
        jobNodeStorage = new JobNodeStorage(regCenter, jobName);
        this.instanceNode = instanceNode;
    }
    
    /**
     * 持久化作业运行实例上线相关信息.
     */
    public void persistOnline() {
        jobNodeStorage.fillEphemeralJobNode(instanceNode.getLocalInstanceNode(), "");
    }
    
    /**
     * 更改节点状态.
     */
    public void updateNodeStatus(ServerStatus status,String instanceId) {
        if(jobNodeStorage.isJobNodeExisted(instanceNode.getInstanceNode(instanceId))){
            jobNodeStorage.updateJobNode(instanceNode.getInstanceNode(instanceId),status.name());
        }
    }

    /**
     * 获取可分片的作业运行实例.
     *
     * @return 可分片的作业运行实例
     */
    public abstract List<JobInstance> getAvailableJobInstances();

    /**
     * 判断节点是否可以有效
     *
     * @return 节点是否可以有效
     */
    public boolean isAvailableJobInstance(String instanceId){
        return !ServerStatus.DISABLED.name().equals(jobNodeStorage.getJobNodeData(instanceNode.getInstanceNode(instanceId)));
    }



    /**
     * 判断当前作业运行实例的节点是否仍然存在.
     * 
     * @return 当前作业运行实例的节点是否仍然存在
     */
    public boolean isLocalJobInstanceExisted() {
        return jobNodeStorage.isJobNodeExisted(instanceNode.getLocalInstanceNode());
    }
}
