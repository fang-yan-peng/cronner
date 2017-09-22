package cronner.jfaster.org.pojo;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author fangyanpeng
 */
@NoArgsConstructor
@Getter
@Setter
public class TaskExecuteInfo {

    int id;

    int jobId;

    String jobName;

    String shardItems;

    int status;

    String hostname;

    String ip;

    int source;

    Integer parentId;

    String failureCause;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date completeTime;

    public TaskExecuteInfo(int id, String hostname, String ip, int source, Date startTime) {
        this.id = id;
        this.hostname = hostname;
        this.ip = ip;
        this.source = source;
        this.startTime = startTime;
    }
}
