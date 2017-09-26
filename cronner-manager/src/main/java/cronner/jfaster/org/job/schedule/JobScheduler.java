package cronner.jfaster.org.job.schedule;

import com.google.common.base.Strings;
import cronner.jfaster.org.exeception.JobSystemException;
import cronner.jfaster.org.executor.JobCompleteHandler;
import cronner.jfaster.org.executor.ScheduleJobFacade;
import cronner.jfaster.org.job.strategy.JobInstance;
import cronner.jfaster.org.model.JobConfiguration;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;
import cronner.jfaster.org.service.TaskService;
import lombok.Getter;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Properties;

/**
 * 作业调度器.
 * @author fangyanpeng
 */
public class JobScheduler {

    private static final String JOB_FACADE_DATA_MAP_KEY = "jobFacade";

    private static final String TASK_SERVICE_DATA_MAP_KEY = "taskService";

    private static final String JOB_ID_DATA_MAP_KEY = "jobId";

    @Getter
    private final JobConfiguration jobConfig;
    
    private final CoordinatorRegistryCenter regCenter;

    private final TaskService taskService;

    @Getter
    private final SchedulerFacade schedulerFacade;
    
    private final ScheduleJobFacade jobFacade;

    
    public JobScheduler(final CoordinatorRegistryCenter regCenter, final JobConfiguration jobConfiguration, int port, TaskService taskService, JobCompleteHandler handler) {
        JobRegistry.getInstance().addJobInstance(jobConfiguration.getJobName(), new JobInstance(port));
        this.jobConfig = jobConfiguration;
        this.regCenter = regCenter;
        this.taskService = taskService;
        schedulerFacade = new SchedulerFacade(regCenter, jobConfiguration.getJobName(),handler);
        jobFacade = new SchedulerJobFacacde(regCenter, jobConfiguration.getJobName());
    }

    
    /**
     * 初始化作业.
     */
    public void init() {
        JobConfiguration jobConfigFromRegCenter = schedulerFacade.updateJobConfiguration(jobConfig);
        JobRegistry.getInstance().setCurrentShardingTotalCount(jobConfigFromRegCenter.getJobName(), jobConfigFromRegCenter.getShardingTotalCount());

        JobScheduleController jobScheduleController;
        if(Strings.isNullOrEmpty(jobConfig.getDependency())){
            jobScheduleController = new JobScheduleController(createScheduler(), createJobDetail(), jobConfigFromRegCenter.getJobName());
        }else {
            jobScheduleController = new JobScheduleController(createScheduleJob());
        }
        JobRegistry.getInstance().registerJob(jobConfigFromRegCenter.getJobName(), jobScheduleController, regCenter);
        schedulerFacade.registerStartUpInfo(true);
        jobScheduleController.scheduleJob(jobConfigFromRegCenter.getCron());
    }

    private CronnerScheduleJob createScheduleJob(){
        CronnerScheduleJob scheduleJob = new CronnerScheduleJob();
        scheduleJob.setJobFacade(jobFacade);
        scheduleJob.setJobId(jobConfig.getId());
        scheduleJob.setTaskService(taskService);
        return scheduleJob;
    }
    
    private JobDetail createJobDetail() {
        JobDetail result = JobBuilder.newJob(CronnerScheduleJob.class).withIdentity(jobConfig.getJobName()).build();
        result.getJobDataMap().put(JOB_FACADE_DATA_MAP_KEY, jobFacade);
        result.getJobDataMap().put(TASK_SERVICE_DATA_MAP_KEY, taskService);
        result.getJobDataMap().put(JOB_ID_DATA_MAP_KEY, jobConfig.getId());
        return result;
    }

    
    private Scheduler createScheduler() {
        Scheduler result;
        try {
            StdSchedulerFactory factory = new StdSchedulerFactory();
            factory.initialize(getBaseQuartzProperties());
            result = factory.getScheduler();
            result.getListenerManager().addTriggerListener(schedulerFacade.newJobTriggerListener());
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
        return result;
    }
    
    private Properties getBaseQuartzProperties() {
        Properties result = new Properties();
        result.put("org.quartz.threadPool.class", org.quartz.simpl.SimpleThreadPool.class.getName());
        result.put("org.quartz.threadPool.threadCount", "1");
        result.put("org.quartz.scheduler.instanceName", jobConfig.getJobName());
        result.put("org.quartz.jobStore.misfireThreshold", "1");
        result.put("org.quartz.plugin.shutdownhook.class", JobShutdownHookPlugin.class.getName());
        result.put("org.quartz.plugin.shutdownhook.cleanShutdown", Boolean.TRUE.toString());
        return result;
    }
}
