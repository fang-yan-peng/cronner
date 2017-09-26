package cronner.jfaster.org.event.type;

import cronner.jfaster.org.context.ExecutionType;
import cronner.jfaster.org.event.JobEvent;
import cronner.jfaster.org.util.IpUtils;
import cronner.jfaster.org.util.date.DateUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

/**
 * 作业状态痕迹事件.
 *
 * @author fangyanpeng
 *
 * */
@Getter
@Setter
@NoArgsConstructor
public final class JobStatusTraceEvent implements JobEvent {

    private String jobName;

    private String currentIp;

    private int taskId;

    private ExecutionType executionType;

    private String shardingItems;

    private State state;

    private String message;

    private Date createTime;

    public JobStatusTraceEvent(String jobName, int taskId, ExecutionType executionType, String shardingItems, State state, String message) {
        this.jobName = jobName;
        this.taskId = taskId;
        this.executionType = executionType;
        this.shardingItems = shardingItems;
        this.state = state;
        this.message = message;
        currentIp = IpUtils.getIp();
        if(state == State.TASK_RUNNING){
            createTime = DateUtil.addSecond(new Date(),1);
        }else if(state == State.TASK_ERROR || state == State.TASK_FINISHED){
            createTime = DateUtil.addSecond(new Date(),2);
        }else {
            createTime = new Date();
        }

    }

    public enum State {
        TASK_STAGING, TASK_RUNNING, TASK_FINISHED, TASK_ERROR,
    }
}
