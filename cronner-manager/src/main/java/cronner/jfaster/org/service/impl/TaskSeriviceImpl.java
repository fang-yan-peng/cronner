package cronner.jfaster.org.service.impl;

import cronner.jfaster.org.dao.TaskDao;
import cronner.jfaster.org.pojo.TaskExecuteInfo;
import cronner.jfaster.org.service.TaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 任务服务实现
 *
 * @author fangyanpeng
 */
@Service
public class TaskSeriviceImpl implements TaskService {

    @Resource
    private TaskDao taskDao;

    /**
     * 添加执行任务
     *
     * @param task 任务实例
     * @return 任务id
     */
    @Override
    public int addTask(TaskExecuteInfo task) {
        return taskDao.addTask(task);
    }

    /**
     * 根据作业名称、开始时间、结束时间，分页获取任务
     * @param jobName 作业名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param start
     * @param pageSize
     * @return 任务列表
     */
    @Override
    public List<TaskExecuteInfo> getTasksByPage(String jobName, Date startTime, Date endTime, int start, int pageSize) {
        return taskDao.getTasksByPage(jobName,startTime,endTime,start,pageSize);
    }

    /**
     *
     * 获取任务数目
     *
     * @param jobName 作业名称
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 任务数
     */
    @Override
    public int getTaskCnt(String jobName, Date startTime, Date endTime) {
        return taskDao.getTaskCnt(jobName,startTime,endTime);
    }

    /**
     * 根据任务父id获取任务
     * @param parentId 父任务id
     * @return 任务
     */
    @Override
    public List<TaskExecuteInfo> getTaskByParentId(int parentId) {
        return taskDao.getTaskByParentId(parentId);
    }

    /**
     * 更新任务执行信息
     * @param task 任务
     * @return 执行结果
     */
    @Override
    public boolean updateTaskExcuteInto(TaskExecuteInfo task) {
        return taskDao.updateTaskExcuteInto(task);
    }

    /**
     * 获取失败信息
     * @param taskId
     * @return
     */
    @Override
    public String getTaskFailCause(int taskId) {
        return taskDao.getTaskFailCause(taskId);
    }
}
