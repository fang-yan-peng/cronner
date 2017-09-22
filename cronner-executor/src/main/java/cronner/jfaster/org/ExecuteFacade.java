package cronner.jfaster.org;

import cronner.jfaster.org.job.instance.ExecuteInstanceService;
import cronner.jfaster.org.job.listener.ExecuteListenerManager;
import cronner.jfaster.org.job.reconcile.ReconcileService;
import cronner.jfaster.org.job.sharding.ShardingService;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;

/**
 * 作业执行门面
 *
 * @author fangyanpeng
 */
public class ExecuteFacade {

    private final ExecuteInstanceService instanceService;

    private final ReconcileService reconcileService;

    private ExecuteListenerManager listenerManager;

    private ShardingService shardingService;

    public ExecuteFacade(final CoordinatorRegistryCenter regCenter, final String jobName) {
        this.instanceService = new ExecuteInstanceService(regCenter,jobName);
        reconcileService = new ReconcileService(regCenter, jobName);
        shardingService = new ShardingService(regCenter,jobName);
        listenerManager = new ExecuteListenerManager(regCenter, jobName);

    }

    /**
     * 注册作业启动信息.
     *
     */
    public void registerStartUpInfo() {
        listenerManager.startAllListeners();
        instanceService.persistOnline();
        shardingService.setReshardingFlag();
        if (!reconcileService.isRunning()) {
            reconcileService.startAsync();
        }
    }

}
