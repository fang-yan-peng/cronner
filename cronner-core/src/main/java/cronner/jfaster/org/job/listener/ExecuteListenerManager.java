package cronner.jfaster.org.job.listener;

import cronner.jfaster.org.job.config.ConfigurationService;
import cronner.jfaster.org.job.failover.FailoverListenerManager;
import cronner.jfaster.org.job.failover.FailoverService;
import cronner.jfaster.org.job.sharding.ExecutionService;
import cronner.jfaster.org.job.sharding.MonitorExecutionListenerManager;
import cronner.jfaster.org.job.sharding.ShardingService;
import cronner.jfaster.org.job.storage.JobNodeStorage;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;


/**
 * 作业注册中心的监听器管理者.
 * @author fangyanpeng
 */
public final class ExecuteListenerManager {

    private final FailoverListenerManager failoverListenerManager;

    private final MonitorExecutionListenerManager monitorExecutionListenerManager;



    public ExecuteListenerManager(final CoordinatorRegistryCenter regCenter, final String jobName) {
        JobNodeStorage jobNodeStorage = new JobNodeStorage(regCenter,jobName);
        ShardingService shardingService = new ShardingService(regCenter,jobName);
        ExecutionService executionService = new ExecutionService(regCenter,jobName);
        FailoverService failoverService = new FailoverService(regCenter,jobName);
        ConfigurationService configService = new ConfigurationService(regCenter,jobName);
        failoverListenerManager = new FailoverListenerManager(jobNodeStorage, jobName,shardingService,failoverService,configService);
        monitorExecutionListenerManager = new MonitorExecutionListenerManager(jobNodeStorage, jobName,executionService);

    }
    
    /**
     * 开启所有监听器.
     */
    public void startAllListeners() {
        monitorExecutionListenerManager.start();
        failoverListenerManager.start();
    }
}
