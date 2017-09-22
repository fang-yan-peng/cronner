package cronner.jfaster.org.executor;

import java.util.Map;

/**
 *
 * @author fangyanpeng
 */
public interface ScheduleJobFacade {
    /**
     * 作业调度,获取在线集群的分片信息.
     *
     * @return 分片上下文
     */
    Map<String,ShardingContexts> scheduleShardingContexts();

    /**
     * 获取作业名称.
     *
     * @return 作业名称
     */
    String getJobName();
}
