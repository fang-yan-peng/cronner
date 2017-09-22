package cronner.jfaster.org.job.election;

import cronner.jfaster.org.exeception.JobConfigurationException;
import cronner.jfaster.org.job.instance.AbstractInstanceNode;
import cronner.jfaster.org.job.schedule.JobRegistry;
import cronner.jfaster.org.job.storage.JobNodeStorage;
import cronner.jfaster.org.job.storage.LeaderExecutionCallback;
import cronner.jfaster.org.model.JobConfiguration;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;
import cronner.jfaster.org.util.BlockUtils;
import cronner.jfaster.org.util.executor.ExecuteThreadService;
import cronner.jfaster.org.util.http.Http;
import cronner.jfaster.org.util.http.HttpMethod;
import cronner.jfaster.org.util.json.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

import static cronner.jfaster.org.constants.CronnerConstant.LEADER_URL;

/**
 * 主节点服务.
 * @author fangyanpeng
 */
@Slf4j
public final class LeaderService {
    
    private final String jobName;

    private final CoordinatorRegistryCenter regCenter;

    private final JobNodeStorage jobNodeStorage;
    
    public LeaderService(final CoordinatorRegistryCenter regCenter, final String jobName) {
        this.regCenter = regCenter;
        this.jobName = jobName;
        jobNodeStorage = new JobNodeStorage(regCenter, jobName);
    }
    
    /**
     * 选举主节点.
     */
    public void electLeader() {
        log.debug("Elect a new leader now.");
        jobNodeStorage.executeInLeader(LeaderNode.LATCH, new LeaderElectionExecutionCallback());
        log.debug("Leader election completed.");
    }
    
    /**
     * 判断当前节点是否是主节点.
     * 
     * <p>
     * 如果主节点正在选举中而导致取不到主节点, 则阻塞至主节点选举完成再返回.
     * </p>
     * 
     * @return 当前节点是否是主节点
     */
    public boolean isLeaderUntilBlock() {
        while (!hasLeader()) {
            log.info("Leader is electing, waiting for {} ms", 100);
            BlockUtils.waitingShortTime();
            if (!JobRegistry.getInstance().isShutdown(jobName) ) {
                electLeader();
            }
        }
        return isLeader();
    }
    
    /**
     * 判断当前节点是否是主节点.
     *
     * @return 当前节点是否是主节点
     */
    public boolean isLeader() {
        return !JobRegistry.getInstance().isShutdown(jobName) && JobRegistry.getInstance().getJobInstance(jobName).getJobInstanceId().equals(jobNodeStorage.getJobNodeData(LeaderNode.INSTANCE));
    }

    /**
     * 通知其他调度节点增加任务
     * @param configuration
     */
    public void notifyOtherScheduler(final JobConfiguration configuration){
        List<String> schedulers = jobNodeStorage.getJobNodeChildrenKeys(AbstractInstanceNode.SCHEDULE_ROOT);
        if(schedulers.isEmpty() || (schedulers.remove(JobRegistry.getInstance().getJobInstance(jobName).getJobInstanceId()) && schedulers.isEmpty())){
            return;
        }
        for (final String scheduler : schedulers){
            try {
                Http http = new Http(String.format(LEADER_URL, scheduler));
                http.method(HttpMethod.PUT);
                http.contentType(Http.JSON_CONNTENT_TYPE);
                http.body(GsonFactory.getGson().toJson(configuration));
                http.request();
            } catch (IOException e) {
                throw new JobConfigurationException(e);
            }
        }
    }

    public String getLeader(){
        return jobNodeStorage.getJobNodeData(LeaderNode.INSTANCE);
    }
    
    /**
     * 判断是否已经有主节点.
     * 
     * @return 是否已经有主节点
     */
    public boolean hasLeader() {
        return jobNodeStorage.isJobNodeExisted(LeaderNode.INSTANCE);
    }
    
    /**
     * 删除主节点供重新选举.
     */
    public void removeLeader() {
        jobNodeStorage.removeJobNodeIfExisted(LeaderNode.INSTANCE);
    }
    
    @RequiredArgsConstructor
    class LeaderElectionExecutionCallback implements LeaderExecutionCallback {
        
        @Override
        public void execute() {
            if (!hasLeader()) {
                jobNodeStorage.fillEphemeralJobNode(LeaderNode.INSTANCE, JobRegistry.getInstance().getJobInstance(jobName).getJobInstanceId());
            }
        }
    }
}
