package cronner.jfaster.org.event;

/**
 * 作业事件监听器配置异常.
 *
 * @author fangyanpeng
 * 
 */
public final class JobEventListenerConfigurationException extends Exception {
    
    private static final long serialVersionUID = 4069519372148227761L;
    
    public JobEventListenerConfigurationException(final Exception ex) {
        super(ex);
    }
}
