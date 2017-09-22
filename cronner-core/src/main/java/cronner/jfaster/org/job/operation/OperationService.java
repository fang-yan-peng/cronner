package cronner.jfaster.org.job.operation;

import cronner.jfaster.org.job.instance.InstanceOperation;
import cronner.jfaster.org.job.storage.JobNodeStorage;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;

/**
 * @author fangyanpeng
 */
public class OperationService {

    private final JobNodeStorage jobNodeStorage;

    private final OperationNode operationNode;

    public OperationService(final CoordinatorRegistryCenter regCenter, final String jobName) {
        operationNode = new OperationNode(jobName);
        jobNodeStorage = new JobNodeStorage(regCenter, jobName);
    }

    public void operation(InstanceOperation operation){
        jobNodeStorage.replaceJobNode(OperationNode.OPERATION,operation.name());
    }

    public void clearTriggerFlag(){
        jobNodeStorage.updateJobNode(OperationNode.OPERATION,"");
    }

    public boolean isOperationPath(String path){
        return operationNode.isOperationPath(path);
    }
}
