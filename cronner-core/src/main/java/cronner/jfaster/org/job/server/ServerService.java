package cronner.jfaster.org.job.server;


import cronner.jfaster.org.job.instance.AbstractInstanceNode;
import cronner.jfaster.org.job.schedule.JobRegistry;
import cronner.jfaster.org.job.storage.JobNodeStorage;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;

/**
 * 作业服务器服务.
 * @author fangyanpeng
 */
public final class ServerService {
    
    private final String jobName;
    
    private final JobNodeStorage jobNodeStorage;
    
    private final ServerNode serverNode;
    
    public ServerService(final CoordinatorRegistryCenter regCenter, final String jobName) {
        this.jobName = jobName;
        jobNodeStorage = new JobNodeStorage(regCenter, jobName);
        serverNode = new ServerNode(jobName);
    }
    
    /**
     * 持久化作业服务器上线信息.
     * 
     * @param enabled 作业是否启用
     */
    public void persistOnline(final boolean enabled) {
        if (!JobRegistry.getInstance().isShutdown(jobName)) {
            jobNodeStorage.fillJobNode(serverNode.getServerNode(JobRegistry.getInstance().getJobInstance(jobName).getIp()), enabled ? "" : ServerStatus.DISABLED.name());
        }
    }

    /**
     * 判断作业服务器是否可用.
     *
     * @param ip 作业服务器IP地址
     * @return 作业服务器是否可用
     */
    public boolean isAvailableServer(final String ip) {
        return isEnableServer(ip) && hasOnlineInstances(ip);
    }

    private boolean hasOnlineInstances(final String ip) {
        for (String each : jobNodeStorage.getJobNodeChildrenKeys(AbstractInstanceNode.SCHEDULE_ROOT)) {
            if (each.startsWith(ip)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断服务器是否启用.
     *
     * @param ip 作业服务器IP地址
     * @return 服务器是否启用
     */
    public boolean isEnableServer(final String ip) {
        return !ServerStatus.DISABLED.name().equals(jobNodeStorage.getJobNodeData(serverNode.getServerNode(ip)));
    }
}
