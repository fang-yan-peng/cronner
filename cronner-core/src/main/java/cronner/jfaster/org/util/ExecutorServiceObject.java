package cronner.jfaster.org.util;

import com.google.common.base.Joiner;
import com.google.common.util.concurrent.MoreExecutors;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;

/**
 * 线程池执行服务对象.
 *
 */
public final class ExecutorServiceObject {
    
    private final ThreadPoolExecutor threadPoolExecutor;
    
    private final BlockingQueue<Runnable> workQueue;
    
    public ExecutorServiceObject(final String namingPattern, final int threadSize) {
        workQueue = new LinkedBlockingQueue<>();
        threadPoolExecutor = new ThreadPoolExecutor(threadSize, threadSize, 5L, TimeUnit.MINUTES, workQueue, 
                new BasicThreadFactory.Builder().namingPattern(Joiner.on("-").join(namingPattern, "%s")).build());
        threadPoolExecutor.allowCoreThreadTimeOut(true);
    }
    
    /**
     * 创建线程池服务对象.
     *
     * @return 线程池服务对象
     */
    public ExecutorService createExecutorService() {
        return MoreExecutors.listeningDecorator(MoreExecutors.getExitingExecutorService(threadPoolExecutor));
    }
    
    public boolean isShutdown() {
        return threadPoolExecutor.isShutdown();
    }
    
    /**
     * 获取当前活跃的线程数.
     *
     * @return 当前活跃的线程数
     */
    public int getActiveThreadCount() {
        return threadPoolExecutor.getActiveCount();
    }
    
    /**
     * 获取待执行任务数量.
     *
     * @return 待执行任务数量
     */
    public int getWorkQueueSize() {
        return workQueue.size();
    }
}
