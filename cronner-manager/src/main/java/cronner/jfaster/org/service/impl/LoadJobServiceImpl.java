package cronner.jfaster.org.service.impl;

import cronner.jfaster.org.dao.JobDao;
import cronner.jfaster.org.executor.JobCompleteHandler;
import cronner.jfaster.org.job.schedule.JobRegistry;
import cronner.jfaster.org.job.schedule.JobScheduler;
import cronner.jfaster.org.model.JobConfiguration;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;
import cronner.jfaster.org.service.LoadJobService;
import cronner.jfaster.org.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @author fangyanpeng
 *
 */
@Slf4j
@Service
public class LoadJobServiceImpl implements LoadJobService {

    @Resource
    private JobDao jobDao;

    @Value("${server.port}")
    private int serverPort;

    @Resource
    private CoordinatorRegistryCenter regCenter;

    @Resource
    private TaskService taskService;

    @Resource
    private JobCompleteHandler handler;

    private static final int pageSize = 100;

    /**
     * 从数据库加载作业
     */
    @Override
    public void load() {
        int jobNum = jobDao.loadJobCnt();
        int pageNum = jobNum % pageSize == 0 ? jobNum / pageSize : jobNum / pageSize +1;
        for (int i = 0; i < pageNum ; ++i){
            List<JobConfiguration> jobs = jobDao.loadJobByPage(i * pageSize,pageSize);
            for (JobConfiguration job : jobs){
                if(JobRegistry.getInstance().isShutdown(job.getJobName())){
                    try {
                        JobScheduler scheduler = new JobScheduler(regCenter,job,serverPort,taskService,handler);
                        scheduler.init();
                        if(!job.isStatus()){
                            JobRegistry.getInstance().getJobScheduleController(job.getJobName()).pauseJob();
                        }
                    } catch (Exception e) {
                        log.error("Load job " + job.getJobName() +" fail: ",e);
                    }
                }
            }
        }

    }
}
