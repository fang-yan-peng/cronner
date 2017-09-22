package cronner.jfaster.org.util.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 *
 * @author fangyanpeng
 */
public class ExecuteThreadService {

    private static final ExecutorService executor;

    static {
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        t.setName("SERVER-SERVICE-WORKER");
                        t.setDaemon(true);
                        return t;
                    }
                });
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                if(!executor.isShutdown()){
                    executor.shutdownNow();
                }
            }
        });
    }

    public static void sumbmit(Runnable task){
        executor.submit(task);
    }

}
