package cronner.jfaster.org.example.job;

import cronner.jfaster.org.job.annotation.Job;
import cronner.jfaster.org.example.job.listener.JobListenerExample;
import cronner.jfaster.org.job.api.ShardingContext;
import cronner.jfaster.org.job.api.dataflow.DataflowJob;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author fangyanpeng
 */
@Job(name = "cronner-dataflow-job",listener = JobListenerExample.class)
public class DataflowCronnerJobSpring implements DataflowJob<String> {

    @Override
    public List<String> fetchData(ShardingContext shardingContext) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList("hello","cronner");
    }

    @Override
    public void processData(ShardingContext shardingContext, List<String> data) {
        System.out.println(String.format("jobName=%s,jobParameter=%s,shardingItem=%s,shardingParameter=%s",shardingContext.getJobName(),shardingContext.getJobParameter(),shardingContext.getShardingItem(),shardingContext.getShardingParameter()));
        for (String str : data){
            System.out.println(str);
        }
    }
}
