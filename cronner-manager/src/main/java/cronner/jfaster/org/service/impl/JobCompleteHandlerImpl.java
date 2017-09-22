package cronner.jfaster.org.service.impl;

import cronner.jfaster.org.executor.JobCompleteHandler;
import cronner.jfaster.org.job.schedule.JobRegistry;
import cronner.jfaster.org.job.schedule.JobScheduleController;
import cronner.jfaster.org.service.JobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author fangyanpeng
 */
@Service
@Slf4j
public class JobCompleteHandlerImpl implements JobCompleteHandler {

    @Resource
    private JobService jobService;

    @Override
    public void complete(String jobName) {
        try {
            JobScheduleController controller = JobRegistry.getInstance().getJobScheduleController(jobName);
            jobService.updateSuccessTime(new Date(),controller.getNextFireTime(),jobName);
        } catch (Exception e) {
            log.error("Job complete deal fail: ",e);
        }
    }
}
