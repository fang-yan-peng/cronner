package cronner.jfaster.org.executor.handler;

import java.util.concurrent.ExecutorService;

/**
 * 线程池服务处理器.
 * 
 * <p>用于作业内部的线程池处理使用.</p>
 *
 * @author fangyanpeng
 *
 */
public interface ExecutorServiceHandler {
    
    /**
     * 创建线程池服务对象.
     * 
     * @param jobName 作业名
     * 
     * @return 线程池服务对象
     */
    ExecutorService createExecutorService(final String jobName);
}
