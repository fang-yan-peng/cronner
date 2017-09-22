package cronner.jfaster.org.constants;

/**
 *
 * 框架常量信息
 *
 * @author fangyanpeng
 *
 */
public class CronnerConstant {

    public static final String DB="cronner";

    public static final String TABLE_JOB="cronner_job";

    public static final String TABLE_TASK="cronner_task";

    public static final String TABLE_TRACE="cronner_task_trace";

    public static final String ZK_NAME="cronner_zookeeper";

    public static final String EXECUTE_URL="http://%s/api/execute/trigger";

    public static final String LEADER_URL = "http://%s/cluster/add";

    public static final String TASK_EXECUTE_EVENT_URL = "http://%s/task/execute_event";

    public static final String TASK_STATUS_EVENT_URL = "http://%s/task/status_event";
}
