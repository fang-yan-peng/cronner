package cronner.jfaster.org.dao;

import cronner.jfaster.org.constants.CronnerConstant;
import cronner.jfaster.org.model.JobConfiguration;
import cronner.jfaster.org.pojo.BriefJobConfig;
import cronner.jfaster.org.pojo.JobConfig;
import org.jfaster.mango.annotation.DB;
import org.jfaster.mango.annotation.ReturnGeneratedId;
import org.jfaster.mango.annotation.SQL;

import java.util.Date;
import java.util.List;

import static cronner.jfaster.org.constants.CronnerConstant.TABLE_JOB;

/**
 * 作业dao
 * @author fangyanpeng
 */
@DB( name= CronnerConstant.DB,table = TABLE_JOB)
public interface JobDao {

    String SELECT_COLS="id,jobName,cron,shardingTotalCount,shardingParameter,jobParameter,jobShardingStrategyClass,failover,allowSendJobEvent,misfire,monitorExecution,description,status,reconcileIntervalMinutes,type,streamingProcess,dependency,createTime,updateTime,lastSuccessTime,nextExecuteTime";

    String SELECT_BRIEF_COLS="jobName,cron,shardingTotalCount,status,type,lastSuccessTime,nextExecuteTime";

    String SELECT_LOAD_COLS="id,jobName,cron,shardingTotalCount,status,shardingParameter,jobParameter,failover,allowSendJobEvent,misfire,monitorExecution,reconcileIntervalMinutes,type,streamingProcess,dependency";

    String INSERT_COLS="jobName,cron,shardingTotalCount,shardingParameter,jobParameter,jobShardingStrategyClass,failover,allowSendJobEvent,misfire,monitorExecution,description,status,reconcileIntervalMinutes,type,streamingProcess,dependency,createTime,updateTime,lastSuccessTime,nextExecuteTime";

    @ReturnGeneratedId
    @SQL("insert into #table("+INSERT_COLS+") values(:jobName,:cron,:shardingTotalCount,:shardingParameter,:jobParameter,:jobShardingStrategyClass,:failover,:allowSendJobEvent,:misfire,:monitorExecution,:description,:status,:reconcileIntervalMinutes,:type,:streamingProcess,:dependency,:createTime,:updateTime,:lastSuccessTime,:nextExecuteTime)")
    int addJob(JobConfig jobConfig);

    @SQL("select "+SELECT_COLS+" from #table where id=:1")
    JobConfig getJobById(int id);

    @SQL("select "+SELECT_COLS+" from #table where jobName=:1")
    JobConfig getJobByName(String jobName);

    @SQL("update #table set cron=:cron,shardingTotalCount=:shardingTotalCount,shardingParameter=:shardingParameter,jobParameter=:jobParameter,failover=:failover,allowSendJobEvent=:allowSendJobEvent,misfire=:misfire,monitorExecution=:monitorExecution,description=:description,streamingProcess=:streamingProcess,type=:type,updateTime=:updateTime where jobName=:jobName")
    boolean updateJob(JobConfig jobConfig);

    @SQL("update #table set status=:1,updateTime=:3 where jobName=:2")
    boolean updateStatus(int status, String jobName, Date updateTime);

    @SQL("update #table set lastSuccessTime=:1,nextExecuteTime=:2 where jobName=:3")
    boolean updateSuccessTime(Date successTime, Date nextExecTime,String jobName);

    @SQL("update #table set lastSuccessTime=:1 where jobName=:2")
    boolean updateSuccessTime(Date successTime, String jobName);

    @SQL("select "+SELECT_BRIEF_COLS+" from #table #if(:1!=null && :1!='') where jobName=:1 #end limit :2,:3")
    List<BriefJobConfig> getJobByPage(String jobName,int start, int pageSize);

    @SQL("select count(*) from #table #if(:1!=null && :1!='') where jobName=:1 #end")
    int getJobCnt(String jobName);

    @SQL("select "+SELECT_LOAD_COLS+" from #table limit :1,:2")
    List<JobConfiguration> loadJobByPage(int start, int pageSize);

    @SQL("select count(*) from #table")
    int loadJobCnt();

    @SQL("delete from #table where jobName = :1")
    void deleteJob(String jobName);

    @SQL("select jobName from #table where dependency=:1 and status=1")
    List<String> getJobsByDep(String dependency);
}
