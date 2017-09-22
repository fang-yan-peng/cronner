package cronner.jfaster.org.job.instance;

import cronner.jfaster.org.job.strategy.JobInstance;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * 执行节点服务类
 *
 * @author fangyanpeng
 *
 */
public class ExecuteInstanceService extends AbstractInstanceService {

    public ExecuteInstanceService(CoordinatorRegistryCenter regCenter, String jobName) {
        super(regCenter, jobName, new ExecuteInstanceNode(jobName));
    }

    @Override
    public List<JobInstance> getAvailableJobInstances() {
        List<JobInstance> result = new LinkedList<>();
        if(!jobNodeStorage.isJobNodeExisted(AbstractInstanceNode.EXECUTE_ROOT)){
            return result;
        }
        for (String each : jobNodeStorage.getJobNodeChildrenKeys(instanceNode.EXECUTE_ROOT)) {
            if(isAvailableJobInstance(each)){
                result.add(new JobInstance(each));
            }
        }
        return result;
    }
}
