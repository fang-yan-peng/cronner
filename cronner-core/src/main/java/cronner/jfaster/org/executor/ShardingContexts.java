package cronner.jfaster.org.executor;

import com.google.common.base.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 分片上下文集合.
 * @author fangyanpeng
 */
@RequiredArgsConstructor
@ToString
public final class ShardingContexts implements Serializable {
    
    private static final long serialVersionUID = -4585977349142082152L;
    
    /**
     * 作业任务ID.
     */
    @Setter
    @Getter
    private int taskId;
    
    /**
     * 作业名称.
     */
    @Setter
    @Getter
    private final String jobName;
    
    /**
     * 分片总数.
     */
    @Setter
    @Getter
    private final int shardingTotalCount;
    
    /**
     * 作业自定义参数.
     * 可以配置多个相同的作业, 但是用不同的参数作为不同的调度实例.
     */
    @Setter
    @Getter
    private final String jobParameter;
    
    /**
     * 分配于本作业实例的分片项和参数的Map.
     */
    @Setter
    @Getter
    private final Map<Integer, String> shardingItemParameters;

    @Setter
    @Getter
    private Map<Integer, Integer> shardingItemTaskIds;
    
    /**
     * 作业事件采样统计数.
     */
    @Setter
    @Getter
    private int jobEventSamplingCount;
    
    /**
     * 当前作业事件采样统计数.
     */
    @Setter
    @Getter
    private int currentJobEventSamplingCount;
    
    /**
     * 是否允许可以发送作业事件.
     */
    @Setter
    @Getter
    private boolean allowSendJobEvent = true;

    /**
     * dataFlow类型作业是否是流处理
     */
    @Setter
    @Getter
    private boolean streamingProcess;

    /**
     * 标识是否来自failover的分片
     */
    @Setter
    @Getter
    private boolean failover;

    public void addItemTaskId(Integer item,Integer taskId){
        if(shardingItemTaskIds == null){
            shardingItemTaskIds = new HashMap<>();
        }
        shardingItemTaskIds.put(item,taskId);
    }

    public Optional<Integer> getItemTaskId(Integer item){
        if(shardingItemTaskIds == null){
            return Optional.absent();
        }
        return Optional.of(shardingItemTaskIds.get(item));
    }



    public ShardingContexts(final int taskId, final String jobName, final int shardingTotalCount, final String jobParameter,
                            final Map<Integer, String> shardingItemParameters, final int jobEventSamplingCount) {
        this.taskId = taskId;
        this.jobName = jobName;
        this.shardingTotalCount = shardingTotalCount;
        this.jobParameter = jobParameter;
        this.shardingItemParameters = shardingItemParameters;
        this.jobEventSamplingCount = jobEventSamplingCount;
    }
}
