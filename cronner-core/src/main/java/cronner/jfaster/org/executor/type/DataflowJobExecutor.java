package cronner.jfaster.org.executor.type;

import cronner.jfaster.org.executor.AbstractCronnerJobExecutor;
import cronner.jfaster.org.executor.JobFacade;
import cronner.jfaster.org.job.api.ShardingContext;
import cronner.jfaster.org.job.api.dataflow.DataflowJob;

import java.util.List;

/**
 * 数据流作业执行器.
 *
 * @author fangyanpeng
 * 
 */
public final class DataflowJobExecutor extends AbstractCronnerJobExecutor {
    
    private final DataflowJob<Object> dataflowJob;
    
    public DataflowJobExecutor(final DataflowJob<Object> dataflowJob, final JobFacade jobFacade) {
        super(jobFacade);
        this.dataflowJob = dataflowJob;
    }
    
    @Override
    protected void process(final ShardingContext shardingContext) {
        if (shardingContext.isStreamingProcess()) {
            streamingExecute(shardingContext);
        } else {
            oneOffExecute(shardingContext);
        }
    }
    
    private void streamingExecute(final ShardingContext shardingContext) {
        List<Object> data = fetchData(shardingContext);
        while (null != data && !data.isEmpty()) {
            processData(shardingContext, data);
            if (!getJobFacade().isEligibleForJobRunning()) {
                break;
            }
            data = fetchData(shardingContext);
        }
    }
    
    private void oneOffExecute(final ShardingContext shardingContext) {
        List<Object> data = fetchData(shardingContext);
        if (null != data && !data.isEmpty()) {
            processData(shardingContext, data);
        }
    }
    
    private List<Object> fetchData(final ShardingContext shardingContext) {
        return dataflowJob.fetchData(shardingContext);
    }
    
    private void processData(final ShardingContext shardingContext, final List<Object> data) {
        dataflowJob.processData(shardingContext, data);
    }
}
