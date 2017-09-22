package cronner.jfaster.org;

import cronner.jfaster.org.event.JobEventBus;
import cronner.jfaster.org.executor.JobFacade;
import cronner.jfaster.org.job.api.CronnerJob;
import cronner.jfaster.org.job.api.listener.JobCompleteDistributeCronnerListener;
import cronner.jfaster.org.job.api.listener.CronnerJobListener;
import cronner.jfaster.org.job.guarantee.GuaranteeService;
import cronner.jfaster.org.job.schedule.CronnerJobFacade;
import cronner.jfaster.org.job.schedule.JobRegistry;
import cronner.jfaster.org.job.strategy.JobInstance;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;
import cronner.jfaster.org.executor.store.JobExecutorRegistry;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 执行器初始化
 *
 * @author fangyanpeng
 */
public class ExecutorInitializer {

    @Getter
    private final ExecuteFacade executeFacade;

    private final JobFacade jobFacade;

    private final String jobName;

    private final CoordinatorRegistryCenter regCenter;

    private final CronnerJob cronnerJob;

    public ExecutorInitializer(CoordinatorRegistryCenter registryCenter, CronnerJob cronnerJob, String jobName, int port,List<CronnerJobListener> cronnerJobListeners){
        this.jobName = jobName;
        regCenter = registryCenter;
        this.cronnerJob = cronnerJob;
        JobRegistry.getInstance().addJobInstance(jobName, new JobInstance(port));
        if(cronnerJobListeners == null){
            cronnerJobListeners = new ArrayList<>(1);
        }
        cronnerJobListeners.add(new JobCompleteDistributeCronnerListener(new GuaranteeService(registryCenter,jobName)));
        executeFacade = new ExecuteFacade(registryCenter, jobName);
        jobFacade = new CronnerJobFacade(registryCenter,jobName,cronnerJobListeners,new JobEventBus(registryCenter,jobName));
    }


    /**
     * 初始化作业.
     */
    public void init() {
        JobRegistry.getInstance().registerJob(jobName, regCenter);
        JobExecutorRegistry.getInstance().registerJobExecutor(jobName,cronnerJob,jobFacade);
        executeFacade.registerStartUpInfo();
    }

}
