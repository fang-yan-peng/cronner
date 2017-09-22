package org.springframework.boot.mango.exeception;

/**
 * @author fangyanpeng
 */
public class MangoSpringBootException extends RuntimeException{

    public MangoSpringBootException(String message) {
        super(message);
    }

    public MangoSpringBootException(String message, Throwable cause) {
        super(message, cause);
    }

    public MangoSpringBootException(Throwable cause) {
        super(cause);
    }

    public MangoSpringBootException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
