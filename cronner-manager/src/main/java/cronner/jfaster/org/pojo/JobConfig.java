package cronner.jfaster.org.pojo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 *
 * 作业配置
 * @author fangyanpeng
 */
@Setter
@Getter
public class JobConfig {

    Integer id;

    String jobName;

    String cron;

    int shardingTotalCount;

    String shardingParameter;

    String jobParameter;

    String jobShardingStrategyClass;

    boolean failover;

    boolean allowSendJobEvent;

    boolean misfire;

    boolean monitorExecution;

    String description;

    Boolean status;

    int reconcileIntervalMinutes;

    int type;

    boolean streamingProcess;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date createTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date updateTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date lastSuccessTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    Date nextExecuteTime;
}
