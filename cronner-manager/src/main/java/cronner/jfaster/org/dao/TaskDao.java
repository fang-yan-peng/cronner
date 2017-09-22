package cronner.jfaster.org.dao;

import cronner.jfaster.org.constants.CronnerConstant;
import cronner.jfaster.org.pojo.TaskExecuteInfo;
import org.jfaster.mango.annotation.DB;
import org.jfaster.mango.annotation.ReturnGeneratedId;
import org.jfaster.mango.annotation.SQL;

import java.util.Date;
import java.util.List;

import static cronner.jfaster.org.constants.CronnerConstant.TABLE_TASK;

/**
 *
 * 任务dao
 * @author fangyanpeng
 */
@DB(name = CronnerConstant.DB,table = TABLE_TASK)
public interface TaskDao {

    String SELECT_COLS="id,jobId,jobName,shardItems,status,hostname,ip,source,parentId,createTime,startTime,completeTime,failureCause";

    String INSERT_COLS = "jobId,jobName,shardItems,status,hostname,ip,source,parentId,createTime,startTime,completeTime,failureCause";

    String SELECT_VIEW_COLS = "id,jobId,jobName,shardItems,createTime";


    @ReturnGeneratedId
    @SQL("insert into #table("+INSERT_COLS+") values(:jobId,:jobName,:shardItems,:status,:hostname,:ip,:source,:parentId,:createTime,:startTime,:completeTime,:failureCause)")
    int addTask(TaskExecuteInfo task);

    @SQL("select "+SELECT_VIEW_COLS+" from #table where jobName=:1 #if(:2!=null && :2!='') and createTime >= :2 #end #if(:3!=null && :3!='') and createTime <= :3 #end and parentId is null order by createTime desc limit :4,:5")
    List<TaskExecuteInfo> getTasksByPage(String jobName, Date startTime,Date endTime,int start,int pageSize);

    @SQL("select count(*) from #table where jobName=:1 #if(:2!=null && :2!='') and createTime >= :2 #end #if(:3!=null && :3!='') and createTime <= :3 #end and parentId is null")
    int getTaskCnt(String jobName, Date startTime,Date endTime);

    @SQL("select "+SELECT_COLS+" from #table where parentId=:1 order by createTime asc")
    List<TaskExecuteInfo> getTaskByParentId(int taskId);

    @SQL("select failureCause from #table where id=:1")
    String getTaskFailCause(int taskId);

    @SQL("update #table set status=:status,hostname=:hostname,ip=:ip,source=:source,startTime=:startTime,completeTime=:completeTime,failureCause=:failureCause where id=:id")
    boolean updateTaskExcuteInto(TaskExecuteInfo task);

}
