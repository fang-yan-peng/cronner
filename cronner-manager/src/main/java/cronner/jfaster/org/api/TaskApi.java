package cronner.jfaster.org.api;

import cronner.jfaster.org.enums.TaskStatus;
import cronner.jfaster.org.event.type.JobExecutionEvent;
import cronner.jfaster.org.event.type.JobStatusTraceEvent;
import cronner.jfaster.org.model.JsonResponse;
import cronner.jfaster.org.pojo.PageParam;
import cronner.jfaster.org.pojo.TaskExecuteInfo;
import cronner.jfaster.org.pojo.TaskTraceEvent;
import cronner.jfaster.org.service.TaskService;
import cronner.jfaster.org.service.TaskTraceService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Strings;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * 任务执行回调接口
 * @author fangyanpeng
 */
@Slf4j
@RestController
@RequestMapping("/task")
public class TaskApi {

    @Resource
    private TaskService taskService;

    @Resource
    private TaskTraceService traceService;

    @RequestMapping(value = "/execute_event",method = RequestMethod.PUT)
    public JsonResponse executeEvent(@RequestBody final JobExecutionEvent event){
        try {
            TaskExecuteInfo info = new TaskExecuteInfo(event.getTaskId(),event.getHostname(),event.getIp(),event.getSource().ordinal(),event.getStartTime());
            if(event.isSuccess()){
                info.setStatus(TaskStatus.SUCCESS.getFlag());
                info.setCompleteTime(event.getCompleteTime());
            }else{
                info.setStatus(TaskStatus.FAIL.getFlag());
                info.setCompleteTime(event.getCompleteTime());
                info.setFailureCause(event.getFailureCause());
            }
            taskService.updateTaskExcuteInto(info);
        } catch (Exception e) {
            log.error("Add execute even fail, jobName: " + event.getJobName(),e);
            return JsonResponse.notOk(e.getMessage());
        }
        return JsonResponse.ok();
    }

    @RequestMapping(value = "/status_event",method = RequestMethod.PUT)
    public JsonResponse statusEvent(@RequestBody final JobStatusTraceEvent event){
        try {
            TaskTraceEvent traceEvent = new TaskTraceEvent(event.getJobName(),event.getCurrentIp(),event.getTaskId(),event.getExecutionType().ordinal(),event.getShardingItems(),event.getState().ordinal(),event.getMessage(),event.getCreateTime());
            traceService.addTraceEvent(traceEvent);
        } catch (Exception e) {
            log.error("Add trace even fail, jobName: " + event.getJobName(),e);
            return JsonResponse.notOk(e.getMessage());
        }
        return JsonResponse.ok();
    }

    @RequestMapping(value = "/get_trace_event/{taskId}")
    public JsonResponse getTraceEvent(@PathVariable("taskId") int taskId){
        try {
            List<TaskTraceEvent> events = traceService.getTraceEventsByTaskId(taskId);
            return JsonResponse.ok(events);
        } catch (Exception e) {
            log.error("Get trace event fail,taskId: " + taskId,e);
            return JsonResponse.notOk(e.getMessage());
        }
    }

    @RequestMapping(value = "/get_execute_event/{taskId}")
    public JsonResponse getExecEvent(@PathVariable("taskId") int taskId){
        try {
            List<TaskExecuteInfo> infos = taskService.getTaskByParentId(taskId);
            return JsonResponse.ok(infos);
        } catch (Exception e) {
            log.error("Get execute event fail,taskId: " + taskId,e);
            return JsonResponse.notOk(e.getMessage());
        }
    }

    @RequestMapping(value = "/get_execute_event_page",method = RequestMethod.GET)
    public JsonResponse getExecEventByPage(@RequestParam String jobName,@RequestParam String startTime,@RequestParam String endTime,@RequestParam int page,@RequestParam int pageSize){
        try {
            if(Strings.isNullOrEmpty(jobName) && Strings.isNullOrEmpty(startTime) && Strings.isNullOrEmpty(endTime)){
                PageParam pageParam = new PageParam(page,pageSize,0);
                return JsonResponse.ok(pageParam);
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date stDate = null;
            if(!Strings.isNullOrEmpty(startTime)){
                stDate = dateFormat.parse(startTime);
            }
            Date enDate = null;
            if(!Strings.isNullOrEmpty(endTime)){
                enDate = dateFormat.parse(endTime);
            }
            if(Strings.isNullOrEmpty(jobName)){
                return JsonResponse.notOk("JobName can not be empty");
            }
            int infoCnt = taskService.getTaskCnt(jobName,stDate,enDate);
            PageParam pageParam = new PageParam(page,pageSize,infoCnt);
            List<TaskExecuteInfo> infos = taskService.getTasksByPage(jobName,stDate,enDate,pageParam.getStart(),pageSize);
            pageParam.setData(infos);
            return JsonResponse.ok(pageParam);
        } catch (Exception e) {
            log.error("get execute event fail: ",e);
            return JsonResponse.notOk(e.getMessage());
        }
    }

}
