package cronner.jfaster.org.service;

import cronner.jfaster.org.pojo.TaskExecuteInfo;

import java.util.Date;
import java.util.List;

/**
 *
 * 任务服务接口
 * @author fangyanpeng
 */
public interface TaskService {

    int addTask(TaskExecuteInfo task);

    List<TaskExecuteInfo> getTasksByPage(String jobName, Date startTime,Date endTime,int start,int pageSize);

    int getTaskCnt(String jobName, Date startTime,Date endTime);

    List<TaskExecuteInfo> getTaskByParentId(int taskId);

    boolean updateTaskExcuteInto(TaskExecuteInfo task);

    String getTaskFailCause(int taskId);

}
