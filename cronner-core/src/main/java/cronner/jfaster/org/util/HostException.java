package cronner.jfaster.org.util;

import java.io.IOException;

/**
 * 网络主机异常.
 *
 * @author fangyanpeng
 */
public final class HostException extends RuntimeException {
    
    private static final long serialVersionUID = 3589264847881174997L;
    
    public HostException(final IOException cause) {
        super(cause);
    }
}
