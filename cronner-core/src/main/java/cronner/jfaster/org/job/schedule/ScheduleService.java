package cronner.jfaster.org.job.schedule;

import com.google.common.base.Optional;
import cronner.jfaster.org.job.election.LeaderNode;
import cronner.jfaster.org.job.election.LeaderService;
import cronner.jfaster.org.job.instance.AbstractInstanceNode;
import cronner.jfaster.org.job.storage.JobNodePath;
import cronner.jfaster.org.job.storage.JobNodeStorage;
import cronner.jfaster.org.job.storage.TransactionExecutionCallback;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;
import cronner.jfaster.org.util.BlockUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.api.transaction.CuratorTransactionFinal;

import java.util.List;
import java.util.Random;

/**
 *
 * 作业调度服务
 * @author fangyanpeng
 */
@Slf4j
public class ScheduleService {

    private final String jobName;

    private final JobNodeStorage jobNodeStorage;

    private final LeaderService leaderService;

    private final JobNodePath jobNodePath;


    public ScheduleService(final CoordinatorRegistryCenter regCenter, final String jobName) {
        this.jobName = jobName;
        jobNodeStorage = new JobNodeStorage(regCenter, jobName);
        leaderService = new LeaderService(regCenter, jobName);
        jobNodePath = new JobNodePath(jobName);

    }

    /**
     *
     * 调度作业
     *
     * @param distributor 作业分发逻辑
     */
    public void leaderSchedule(ScheduleDistributor distributor) {
        if(!isNeedSchedule()){
            return;
        }
        if (!leaderService.isLeaderUntilBlock()) {
            blockUntilScheduleCompleted();
        }
        if(!isNeedSchedule()){
            return;
        }
        jobNodeStorage.fillEphemeralJobNode(ScheduleNode.SCHEDULING, "");
        distributor.schedule();
        removeScheduleFlag();

    }

    /**
     * 获取一个调度节点，优先从节点
     *
     * @return 调度节点host
     */
    public Optional<String> getSchedulerHost(){
        List<String> schedulerHosts = jobNodeStorage.getJobNodeChildrenKeys(AbstractInstanceNode.SCHEDULE_ROOT);
        if (schedulerHosts.isEmpty()) {
            return Optional.absent();
        }
        if(jobNodeStorage.isJobNodeExisted(LeaderNode.INSTANCE)) {
            String leaderSchedulerHost = jobNodeStorage.getJobNodeData(LeaderNode.INSTANCE);
            if ((schedulerHosts.remove(leaderSchedulerHost) && schedulerHosts.isEmpty())) {
                return Optional.of(leaderSchedulerHost);
            }
        }
        int index = new Random().nextInt(schedulerHosts.size());
        return Optional.of(schedulerHosts.get(index));
    }

    /**
     *
     * 判断是否需要调度
     *
     * @return 调度
     */
    public boolean isNeedSchedule() {
        return jobNodeStorage.isJobNodeExisted(ScheduleNode.NECESSARY);
    }


    /**
     * 设置调度标志位
     */
    public void setScheduleFlag(){
        jobNodeStorage.createJobNodeIfNeeded(ScheduleNode.NECESSARY);
    }

    /**
     * 移除调度标志位
     */
    public void removeScheduleFlag(){
        jobNodeStorage.executeInTransaction(new ClearScheduleFlagTransactionExecutionCallback());
    }

    public void blockUntilScheduleCompleted() {
        while (!leaderService.isLeaderUntilBlock() && (isNeedSchedule() || jobNodeStorage.isJobNodeExisted(ScheduleNode.SCHEDULING))) {
            log.debug("Job '{}' sleep short time until scheduling completed.", jobName);
            BlockUtils.waitingShortTime();
        }
    }

    public interface ScheduleDistributor{
        void schedule();
    }

    class ClearScheduleFlagTransactionExecutionCallback implements TransactionExecutionCallback {
        @Override
        public void execute(final CuratorTransactionFinal curatorTransactionFinal) throws Exception {
            curatorTransactionFinal.delete().forPath(jobNodePath.getFullPath(ScheduleNode.NECESSARY)).and();
            curatorTransactionFinal.delete().forPath(jobNodePath.getFullPath(ScheduleNode.SCHEDULING)).and();
        }
    }
}
