package cronner.jfaster.org.job.schedule;

import cronner.jfaster.org.executor.JobCompleteHandler;
import cronner.jfaster.org.job.config.ConfigurationService;
import cronner.jfaster.org.job.election.LeaderService;
import cronner.jfaster.org.job.instance.ScheduleInstanceService;
import cronner.jfaster.org.job.listener.ScheduleListenerManager;
import cronner.jfaster.org.job.server.ServerService;
import cronner.jfaster.org.job.sharding.ExecutionService;
import cronner.jfaster.org.job.sharding.ShardingService;
import cronner.jfaster.org.model.JobConfiguration;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;

/**
 * 为调度器提供内部服务的门面类.
 * @author fangyanpeng
 */
public final class SchedulerFacade {

    private final ConfigurationService configService;
    
    private final LeaderService leaderService;
    
    private final ServerService serverService;

    private final ShardingService shardingService;

    private final ExecutionService executionService;

    private final ScheduleInstanceService instanceService;
    
    private ScheduleListenerManager listenerManager;

    public SchedulerFacade(final CoordinatorRegistryCenter regCenter, final String jobName , final JobCompleteHandler handler) {
        configService = new ConfigurationService(regCenter, jobName);
        leaderService = new LeaderService(regCenter, jobName);
        serverService = new ServerService(regCenter, jobName);
        shardingService = new ShardingService(regCenter, jobName);
        executionService = new ExecutionService(regCenter, jobName);
        instanceService = new ScheduleInstanceService(regCenter,jobName);
        listenerManager = new ScheduleListenerManager(regCenter, jobName,handler,leaderService,shardingService,serverService,executionService);

    }
    
    /**
     * 获取作业触发监听器.
     *
     * @return 作业触发监听器
     */
    public JobTriggerListener newJobTriggerListener() {
        return new JobTriggerListener(executionService,shardingService);
    }
    
    /**
     * 更新作业配置.
     *
     * @param jobConfiguration 作业配置
     * @return 更新后的作业配置
     */
    public JobConfiguration updateJobConfiguration(final JobConfiguration jobConfiguration) {
        configService.persist(jobConfiguration);
        return configService.load(false);
    }
    
    /**
     * 注册作业启动信息.
     * 
     * @param enabled 作业是否启用
     */
    public void registerStartUpInfo(final boolean enabled) {
        listenerManager.startAllListeners();
        leaderService.electLeader();
        instanceService.persistOnline();
        serverService.persistOnline(enabled);
    }
}
