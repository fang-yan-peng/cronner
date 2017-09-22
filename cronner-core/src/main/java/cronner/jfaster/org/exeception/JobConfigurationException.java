package cronner.jfaster.org.exeception;

/**
 * 作业配置异常.
 *
 * @author fangyanpeng
 * 
 */
public final class JobConfigurationException extends RuntimeException {
    
    private static final long serialVersionUID = 3244988974343209468L;
    
    public JobConfigurationException(final String errorMessage, final Object... args) {
        super(String.format(errorMessage, args));
    }
    
    public JobConfigurationException(final Throwable cause) {
        super(cause);
    }
}
