package cronner.jfaster.org.model;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * 作业配置
 * @author fangyanpeng
 */
@Setter
@Getter
public class JobConfiguration {

    Integer id;

    String jobName;

    String cron;

    int shardingTotalCount;

    String shardingParameter;

    String jobParameter;

    boolean failover;

    boolean allowSendJobEvent;

    boolean misfire;

    boolean status;

    boolean monitorExecution;

    Integer reconcileIntervalMinutes;

    Integer type;

    boolean streamingProcess;

}
