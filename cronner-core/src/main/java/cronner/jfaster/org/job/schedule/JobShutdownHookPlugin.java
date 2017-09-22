package cronner.jfaster.org.job.schedule;

import cronner.jfaster.org.job.election.LeaderService;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.plugins.management.ShutdownHookPlugin;
import org.quartz.spi.ClassLoadHelper;

/**
 * 作业关闭钩子.
 * @author fangyanpeng
 */
public final class JobShutdownHookPlugin extends ShutdownHookPlugin {
    
    private String jobName;
    
    @Override
    public void initialize(final String name, final Scheduler scheduler, final ClassLoadHelper classLoadHelper) throws SchedulerException {
        super.initialize(name, scheduler, classLoadHelper);
        jobName = scheduler.getSchedulerName();
    }
    
    @Override
    public void shutdown() {
        CoordinatorRegistryCenter regCenter = JobRegistry.getInstance().getRegCenter(jobName);
        if (null == regCenter) {
            return;
        }
        LeaderService leaderService = new LeaderService(regCenter, jobName);
        if (leaderService.isLeader()) {
            leaderService.removeLeader();
        }
    }
}
