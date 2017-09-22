package cronner.jfaster.org.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 任务执行轨迹事件
 * @author fangyanpeng
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskTraceEvent {

    private String jobName;

    private String currentIp;

    private int taskId;

    private int executionType;

    private String shardingItems;

    private int state;

    private String message;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
