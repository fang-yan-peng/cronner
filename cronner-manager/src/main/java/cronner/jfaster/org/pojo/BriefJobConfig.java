package cronner.jfaster.org.pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author fangyanpeng
 */
@Getter
@Setter
public class BriefJobConfig {

    String jobName;

    String cron;

    int shardingTotalCount;

    boolean status;

    int type;

    Date lastSuccessTime;

    Date nextExecuteTime;
}
