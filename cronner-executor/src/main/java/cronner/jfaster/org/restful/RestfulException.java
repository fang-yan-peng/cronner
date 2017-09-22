package cronner.jfaster.org.restful;

/**
 * REST API异常.
 *
 * @author fangyanpeng
 */
public final class RestfulException extends RuntimeException {
    
    private static final long serialVersionUID = -7594937349408972960L;
    
    public RestfulException(final Throwable cause) {
        super(cause);
    }
}
