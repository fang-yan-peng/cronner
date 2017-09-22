package cronner.jfaster.org.service.impl;

import cronner.jfaster.org.dao.TaskTraceDao;
import cronner.jfaster.org.pojo.TaskTraceEvent;
import cronner.jfaster.org.service.TaskTraceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fangyanpeng
 */
@Service
public class TaskTraceServiceImpl implements TaskTraceService {

    @Resource
    private TaskTraceDao traceDao;

    /**
     * 添加运行事件
     * @param event 运行事件
     * @return 添加结果
     */
    @Override
    public boolean addTraceEvent(TaskTraceEvent event) {
        return traceDao.addTraceEvent(event);
    }

    /**
     * 根据taskid获取执行轨迹
     * @param taskId 任务id
     * @return 运行轨迹
     */
    @Override
    public List<TaskTraceEvent> getTraceEventsByTaskId(int taskId) {
        return traceDao.getTraceEventsByTaskId(taskId);
    }
}
