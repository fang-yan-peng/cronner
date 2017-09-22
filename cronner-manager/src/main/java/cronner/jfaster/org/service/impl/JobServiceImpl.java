package cronner.jfaster.org.service.impl;

import cronner.jfaster.org.dao.JobDao;
import cronner.jfaster.org.model.JobConfiguration;
import cronner.jfaster.org.pojo.BriefJobConfig;
import cronner.jfaster.org.pojo.JobConfig;
import cronner.jfaster.org.service.JobService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author fangyanpeng
 */
@Service
public class JobServiceImpl implements JobService {

    @Resource
    private JobDao jobDao;

    /**
     * 添加作业配置
     * @param jobConfiguration 作业配置
     * @return
     */
    @Override
    public int addJob(JobConfig jobConfiguration) {
        return jobDao.addJob(jobConfiguration);
    }

    /**
     * 根据作业id获取作业配置
     * @param id 作业id
     * @return 作业配置
     */
    @Override
    public JobConfig getJobById(int id) {
        return jobDao.getJobById(id);
    }

    /**
     * 根据作业名称获取作业配置
     * @param jobName 作业名称
     * @return 作业配置
     */
    @Override
    public JobConfig getJobByName(String jobName) {
        return jobDao.getJobByName(jobName);
    }

    /**
     * 更新配置
     * @param jobConfiguration 作业配置
     * @return 更新结果
     */
    @Override
    public boolean updateJob(JobConfig jobConfiguration) {
        return jobDao.updateJob(jobConfiguration);
    }

    /**
     * 更新作业状态
     * @param status 状态
     * @param jobName 作业名
     * @param updateTime 更新时间
     * @return 更新结果
     */
    @Override
    public boolean updateStatus(int status, String jobName, Date updateTime) {
        return jobDao.updateStatus(status,jobName,updateTime);
    }

    /**
     *
     * 更新成功时间
     * @param successTime 成功时间
     * @param nextExecTime 下一次运行时间
     * @param jobName 作业名称
     * @return 更新结果
     */
    @Override
    public boolean updateSuccessTime(Date successTime, Date nextExecTime, String jobName) {
        return jobDao.updateSuccessTime(successTime,nextExecTime,jobName);
    }

    /**
     *
     * 分页获取作业
     *
     * @param jobName 作业名称
     * @param start  开始
     * @param pageSize 分页大小
     * @return 作业列表
     */
    @Override
    public List<BriefJobConfig> getJobByPage(String jobName,int start, int pageSize) {
        return jobDao.getJobByPage(jobName,start,pageSize);
    }

    /**
     *
     * 获取作业总数
     *
     * @param jobName 作业名称
     * @return 作业总数
     */
    @Override
    public int getJobCnt(String jobName) {
        return jobDao.getJobCnt(jobName);
    }

    /**
     * 根据作业名删除作业
     * @param jobName 作业名称
     */
    @Override
    public void deleteJob(String jobName) {
        jobDao.deleteJob(jobName);
    }
}
