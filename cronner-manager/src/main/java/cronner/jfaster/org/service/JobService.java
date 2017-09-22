package cronner.jfaster.org.service;

import cronner.jfaster.org.model.JobConfiguration;
import cronner.jfaster.org.pojo.BriefJobConfig;
import cronner.jfaster.org.pojo.JobConfig;

import java.util.Date;
import java.util.List;

/**
 *
 * 作业服务接口
 *
 */
public interface JobService {

    int addJob(JobConfig jobConfig);

    JobConfig getJobById(int id);

    JobConfig getJobByName(String jobName);

    boolean updateJob(JobConfig jobConfig);

    boolean updateStatus(int status, String jobName, Date updateTime);

    boolean updateSuccessTime(Date successTime, Date nextExecTime,String jobName);

    List<BriefJobConfig> getJobByPage(String jobName,int start, int pageSize);

    int getJobCnt(String jobName);

    void deleteJob(String jobName);
}
