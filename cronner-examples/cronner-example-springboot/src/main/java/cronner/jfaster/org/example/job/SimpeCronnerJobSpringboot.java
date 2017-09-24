package cronner.jfaster.org.example.job;

import cronner.jfaster.org.job.annotation.Job;
import cronner.jfaster.org.example.job.listener.JobListenerExample;
import cronner.jfaster.org.job.api.ShardingContext;
import cronner.jfaster.org.job.api.simple.SimpleJob;

import java.util.concurrent.TimeUnit;

/**
 * @author fangyanpeng
 */
@Job(name = "cronner-simple-job",listener = JobListenerExample.class)
public class SimpeCronnerJobSpringboot implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println(String.format("jobName=%s,jobParameter=%s,shardingItem=%s,shardingParameter=%s",shardingContext.getJobName(),shardingContext.getJobParameter(),shardingContext.getShardingItem(),shardingContext.getShardingParameter()));

        //模拟job执行时间
        int i = 1;
        while(i <= 10) {
            ++i;
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
