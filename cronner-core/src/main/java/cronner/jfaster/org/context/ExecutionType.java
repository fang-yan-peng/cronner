package cronner.jfaster.org.context;

/**
 * 执行类型.
 *
 * @author fangyanpeng
 *
 */
public enum ExecutionType {
    
    /**
     * 正常执行的任务.
     */
    NORMAL,
    
    /**
     * 失效转移的任务.
     */
    FAILOVER
}
