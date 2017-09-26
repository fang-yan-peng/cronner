package cronner.jfaster.org.service.impl;

import cronner.jfaster.org.executor.JobCompleteHandler;
import cronner.jfaster.org.job.schedule.JobRegistry;
import cronner.jfaster.org.job.schedule.JobScheduleController;
import cronner.jfaster.org.service.JobService;
import cronner.jfaster.org.util.executor.ExecuteThreadService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author fangyanpeng
 */
@Service
@Slf4j
public class JobCompleteHandlerImpl implements JobCompleteHandler {

    @Resource
    private JobService jobService;


    @Override
    public void complete(final String jobName) {
        try {
            ExecuteThreadService.sumbmit(new Runnable() {
                @Override
                public void run() {
                    try {
                        JobScheduleController controller = JobRegistry.getInstance().getJobScheduleController(jobName);
                        if(controller == null){
                            return;
                        }
                        Date nextFireTime = controller.getNextFireTime();
                        Date successTime = new Date();
                        if(nextFireTime == null){
                            jobService.updateSuccessTime(successTime,jobName);
                        }else {
                            jobService.updateSuccessTime(successTime,controller.getNextFireTime(),jobName);
                        }
                        /**
                         * 获取依赖的作业
                         */
                        List<String> jobs = jobService.getJobsByDep(jobName);
                        if(jobs != null && !jobs.isEmpty()){
                            for(String job : jobs){
                                JobScheduleController control = JobRegistry.getInstance().getJobScheduleController(job);
                                if(job == null){
                                    continue;
                                }
                                try {
                                    control.executeJob();
                                } catch (JobExecutionException e) {
                                    log.error(String.format("Job %s depend on %s execute fail: ",job,jobName),e);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        log.error(String.format("Job %s complete deal fail:",jobName),e);
                    }
                }
            });

        } catch (Exception e) {
            log.error(String.format("Submit job %s complete fail: ",jobName),e);
        }
    }
}
