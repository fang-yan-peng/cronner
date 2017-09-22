package cronner.jfaster.org.event.type;

import cronner.jfaster.org.event.JobEvent;
import cronner.jfaster.org.exeception.ExceptionUtil;
import cronner.jfaster.org.util.IpUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * 作业执行事件.
 *
 * @author fangyanpeng
 */
@RequiredArgsConstructor
@Setter
@Getter
public final class JobExecutionEvent implements JobEvent {

    private String hostname = IpUtils.getHostName();

    private String ip = IpUtils.getIp();

    private final int taskId;

    private final String jobName;

    private final ExecutionSource source;

    private final int shardingItem;

    private Date startTime = new Date();

    private Date completeTime;

    private boolean success;

    private String failureCause;

    public JobExecutionEvent(){
        taskId = 0;
        jobName = null;
        source = null;
        shardingItem = 0;
    }

    /**
     * 作业执行成功.
     * 
     * @return 作业执行事件
     */
    public JobExecutionEvent executionSuccess() {
        this.setSuccess(true);
        this.setCompleteTime(new Date());
        return this;
    }
    
    /**
     * 作业执行失败.
     * 
     * @param failureCause 失败原因
     * @return 作业执行事件
     */
    public JobExecutionEvent executionFailure(final Throwable failureCause) {
        this.setSuccess(false);
        this.setCompleteTime(new Date());
        this.setFailureCause(ExceptionUtil.transform(failureCause == null ? null : failureCause));
        return this;
    }

    
    /**
     * 执行来源.
     */
    public enum ExecutionSource {
        NORMAL_TRIGGER, MISFIRE, FAILOVER
    }
}
