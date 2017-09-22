package cronner.jfaster.org.executor.handler.impl;

import cronner.jfaster.org.executor.handler.JobExceptionHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认作业异常处理器.
 *
 * @author fangyanpeng
 */
@Slf4j
public final class DefaultJobExceptionHandler implements JobExceptionHandler {
    
    @Override
    public void handleException(final String jobName, final Throwable cause) {
        log.error(String.format("Job '%s' exception occur in job processing", jobName), cause);
    }
}
