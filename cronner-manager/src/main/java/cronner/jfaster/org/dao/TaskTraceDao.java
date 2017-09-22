package cronner.jfaster.org.dao;

import cronner.jfaster.org.constants.CronnerConstant;
import cronner.jfaster.org.pojo.TaskTraceEvent;
import org.jfaster.mango.annotation.DB;
import org.jfaster.mango.annotation.SQL;

import java.util.List;

import static cronner.jfaster.org.constants.CronnerConstant.TABLE_TRACE;

/**
 * 任务执行轨迹dao
 *
 * @author fangyanpeng
 */
@DB(name = CronnerConstant.DB,table = TABLE_TRACE)
public interface TaskTraceDao {

    String COLUME="jobName,currentIp,taskId,executionType,shardingItems,state,message,createTime";

    @SQL("insert into #table (" + COLUME +") values (:jobName,:currentIp,:taskId,:executionType,:shardingItems,:state,:message,:createTime)")
    boolean addTraceEvent(TaskTraceEvent event);

    @SQL("select " + COLUME + " from #table where taskId=:1 order by createTime asc")
    List<TaskTraceEvent> getTraceEventsByTaskId(int taskId);

}
