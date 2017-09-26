package cronner.jfaster.org.job.schedule;

import cronner.jfaster.org.exeception.JobSystemException;
import cronner.jfaster.org.util.executor.ExecuteThreadService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.Date;

/**
 * 作业调度控制器.
 * @author fangyanpeng
 */
@Slf4j
public final class JobScheduleController {
    
    private Scheduler scheduler;
    
    private JobDetail jobDetail;

    private String triggerIdentity;

    private Job job;

    @Getter
    private boolean dependency;

    public JobScheduleController(Scheduler scheduler,JobDetail jobDetail,String triggerIdentity){
        this.scheduler = scheduler;
        this.jobDetail = jobDetail;
        this.triggerIdentity = triggerIdentity;
    }

    public JobScheduleController(Job job){
        this.job = job;
        dependency = true;
    }

    public void executeJob() throws JobExecutionException {
        if(job != null){
            job.execute(null);
        }
    }
    
    /**
     * 调度作业.
     * 
     * @param cron CRON表达式
     */
    public void scheduleJob(final String cron) {
        try {
            if(dependency){
                return;
            }
            if (!scheduler.checkExists(jobDetail.getKey())) {
                scheduler.scheduleJob(jobDetail, createTrigger(cron));
            }
            scheduler.start();
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }
    
    /**
     * 重新调度作业.
     * 
     * @param cron CRON表达式
     */
    public synchronized void rescheduleJob(final String cron) {
        try {
            if(dependency){
                return;
            }
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey(triggerIdentity));
            if (!scheduler.isShutdown() && null != trigger && !cron.equals(trigger.getCronExpression())) {
                scheduler.rescheduleJob(TriggerKey.triggerKey(triggerIdentity), createTrigger(cron));
            }
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }

    /**
     * 获取作业的下一次触发时间
     *
     * @return
     */
    public synchronized Date getNextFireTime() {
        try {
            if(dependency){
                return null;
            }
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(TriggerKey.triggerKey(triggerIdentity));
            return trigger.getNextFireTime();
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }
    
    private CronTrigger createTrigger(final String cron) {
        return TriggerBuilder.newTrigger().withIdentity(triggerIdentity).withSchedule(CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing()).build();
    }
    
    /**
     * 判断作业是否暂停.
     * 
     * @return 作业是否暂停
     */
    public synchronized boolean isPaused() {
        try {
            if(dependency){
                return false;
            }
            return !scheduler.isShutdown() && Trigger.TriggerState.PAUSED == scheduler.getTriggerState(new TriggerKey(triggerIdentity));
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }
    
    /**
     * 暂停作业.
     */
    public synchronized void pauseJob() {
        try {
            if(dependency){
                return;
            }
            if (!scheduler.isShutdown()) {
                scheduler.pauseAll();
            }
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }
    
    /**
     * 恢复作业.
     */
    public synchronized void resumeJob() {
        try {
            if(dependency){
                return;
            }
            if (!scheduler.isShutdown()) {
                scheduler.resumeAll();
            }
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }
    
    /**
     * 立刻启动作业.
     */
    public synchronized void triggerJob() {
        try {
            if(dependency){
                if(job != null){
                    ExecuteThreadService.sumbmit(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                job.execute(null);
                            } catch (JobExecutionException e) {
                                log.error("Trigger job with dependency fail: ",e);
                            }
                        }
                    });
                }
                return;
            }
            if (!scheduler.isShutdown()) {
                scheduler.triggerJob(jobDetail.getKey());
            }
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }
    
    /**
     * 关闭调度器.
     */
    public synchronized void shutdown() {
        try {
            if(dependency){
                return;
            }
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (final SchedulerException ex) {
            throw new JobSystemException(ex);
        }
    }
}
