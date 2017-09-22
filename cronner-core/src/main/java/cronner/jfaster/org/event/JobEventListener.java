package cronner.jfaster.org.event;


import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import cronner.jfaster.org.event.type.JobExecutionEvent;
import cronner.jfaster.org.event.type.JobStatusTraceEvent;

/**
 * 作业事件监听器.
 *
 * @author fangyanpeng
 *
 */
public interface JobEventListener{
    
    /**
     * 作业执行事件监听执行.
     *
     * @param jobExecutionEvent 作业执行事件
     */
    @Subscribe
    @AllowConcurrentEvents
    void listen(JobExecutionEvent jobExecutionEvent);
    
    /**
     * 作业状态痕迹事件监听执行.
     *
     * @param jobStatusTraceEvent 作业状态痕迹事件
     */
    @Subscribe
    @AllowConcurrentEvents
    void listen(JobStatusTraceEvent jobStatusTraceEvent);
}
