package cronner.jfaster.org.service;

import cronner.jfaster.org.pojo.TaskTraceEvent;

import java.util.List;

/**
 * @author fangyanpeng
 */
public interface TaskTraceService {

    boolean addTraceEvent(TaskTraceEvent event);

    List<TaskTraceEvent> getTraceEventsByTaskId(int taskId);
}
