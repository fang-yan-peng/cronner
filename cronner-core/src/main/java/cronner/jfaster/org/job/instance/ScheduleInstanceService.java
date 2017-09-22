package cronner.jfaster.org.job.instance;

import cronner.jfaster.org.job.strategy.JobInstance;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;

import java.util.Collections;
import java.util.List;

/**
 *
 * 调度节点服务类
 *
 * @author fangyanpeng
 *
 */
public class ScheduleInstanceService extends AbstractInstanceService {

    public ScheduleInstanceService(CoordinatorRegistryCenter regCenter, String jobName) {
        super(regCenter, jobName, new ScheduleInstanceNode(jobName));
    }

    @Override
    public List<JobInstance> getAvailableJobInstances() {
        return Collections.emptyList();
    }
}
