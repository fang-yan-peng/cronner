package cronner.jfaster.org.event;

/**
 * 作业事件接口.
 *
 * @author fangyanpeng
 *
 */
public interface JobEvent {
    
    /**
     * 获取作业名称.
     * 
     * @return 作业名称
     */
    String getJobName();
}
