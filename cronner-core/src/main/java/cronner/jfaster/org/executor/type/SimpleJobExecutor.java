package cronner.jfaster.org.executor.type;


import cronner.jfaster.org.executor.AbstractCronnerJobExecutor;
import cronner.jfaster.org.executor.JobFacade;
import cronner.jfaster.org.job.api.ShardingContext;
import cronner.jfaster.org.job.api.simple.SimpleJob;

/**
 * 简单作业执行器.
 *
 * @author fangyanpeng
 * 
 */
public final class SimpleJobExecutor extends AbstractCronnerJobExecutor {
    
    private final SimpleJob simpleJob;
    
    public SimpleJobExecutor(final SimpleJob simpleJob, final JobFacade jobFacade) {
        super(jobFacade);
        this.simpleJob = simpleJob;
    }
    
    @Override
    protected void process(final ShardingContext shardingContext) {
        simpleJob.execute(shardingContext);
    }
}
