package cronner.jfaster.org.executor.handler.impl;


import cronner.jfaster.org.executor.handler.ExecutorServiceHandler;
import cronner.jfaster.org.util.ExecutorServiceObject;

import java.util.concurrent.ExecutorService;

/**
 * 默认线程池服务处理器.
 * @author fangyanpeng
 */
public final class DefaultExecutorServiceHandler implements ExecutorServiceHandler {
    
    @Override
    public ExecutorService createExecutorService(final String jobName) {
        return new ExecutorServiceObject("inner-job-" + jobName, Runtime.getRuntime().availableProcessors() * 2).createExecutorService();
    }
}
