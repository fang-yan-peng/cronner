package cronner.jfaster.org.example.job.listener;

import cronner.jfaster.org.executor.ShardingContexts;
import cronner.jfaster.org.job.api.listener.CronnerJobListener;

/**
 * @author fangyanpeng
 */

public class JobListenerExample implements CronnerJobListener{

    @Override
    public void beforeJobExecuted(ShardingContexts shardingContexts) {
        System.out.println(String.format("----Spring Job: %s begin----",shardingContexts.getJobName()));
    }

    @Override
    public void afterJobExecuted(ShardingContexts shardingContexts) {
        System.out.println(String.format("----Spring Job: %s end----",shardingContexts.getJobName()));
    }
}
