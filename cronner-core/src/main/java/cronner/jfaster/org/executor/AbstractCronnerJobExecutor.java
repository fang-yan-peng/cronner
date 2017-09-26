package cronner.jfaster.org.executor;

import com.google.common.base.Joiner;
import cronner.jfaster.org.context.ExecutionType;
import cronner.jfaster.org.event.type.JobExecutionEvent;
import cronner.jfaster.org.event.type.JobStatusTraceEvent;
import cronner.jfaster.org.exeception.JobSystemException;
import cronner.jfaster.org.executor.handler.ExecutorServiceHandler;
import cronner.jfaster.org.executor.handler.ExecutorServiceHandlerRegistry;
import cronner.jfaster.org.executor.handler.JobExceptionHandler;
import cronner.jfaster.org.job.api.ShardingContext;
import cronner.jfaster.org.job.config.JobProperties;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

/**
 * 弹性化分布式作业执行器.
 *
 * @author fangyanpeng
 *
 */
@Slf4j
public abstract class AbstractCronnerJobExecutor {
    
    @Getter(AccessLevel.PROTECTED)
    private final JobFacade jobFacade;

    private final String jobName;
    
    private final ExecutorService executorService;
    
    private final JobExceptionHandler jobExceptionHandler;

    private boolean error = false;

    protected AbstractCronnerJobExecutor(final JobFacade jobFacade) {
        this.jobFacade = jobFacade;
        jobName = jobFacade.getJobName();
        executorService = ExecutorServiceHandlerRegistry.getExecutorServiceHandler(jobName, (ExecutorServiceHandler) getHandler(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER));
        jobExceptionHandler = (JobExceptionHandler) getHandler(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER);
    }
    
    private Object getHandler(final JobProperties.JobPropertiesEnum jobPropertiesEnum) {
        return getDefaultHandler(jobPropertiesEnum, "");
    }
    
    private Object getDefaultHandler(final JobProperties.JobPropertiesEnum jobPropertiesEnum, final String handlerClassName) {
        try {
            return Class.forName(jobPropertiesEnum.getDefaultValue()).newInstance();
        } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new JobSystemException(e);
        }
    }
    
    /**
     * 执行作业.
     */
    public final void execute(ShardingContexts shardingContexts) {
        ExecutionType executionType = shardingContexts.isFailover() ? ExecutionType.FAILOVER : ExecutionType.NORMAL;
        String shardingItems = Joiner.on(",").join(shardingContexts.getShardingItemParameters().keySet());
        if (shardingContexts.isAllowSendJobEvent()) {
            JobStatusTraceEvent event = new JobStatusTraceEvent(jobName,shardingContexts.getTaskId(),executionType, shardingItems, JobStatusTraceEvent.State.TASK_STAGING,String.format("Job '%s' execute begin.", jobName));
            jobFacade.postJobStatusTraceEvent(event);
        }
        if (jobFacade.misfireIfRunning(shardingContexts.getShardingItemParameters().keySet())) {
            if (shardingContexts.isAllowSendJobEvent()) {
                JobStatusTraceEvent event = new JobStatusTraceEvent(jobName,shardingContexts.getTaskId(),executionType, shardingItems, JobStatusTraceEvent.State.TASK_FINISHED,String.format("Previous job '%s' - shardingItems '%s' is still running, misfired job will start after previous job completed."));
                jobFacade.postJobStatusTraceEvent(event);
            }
            return;
        }
        try {
            jobFacade.beforeJobExecuted(shardingContexts);
            //CHECKSTYLE:OFF
        } catch (final Throwable cause) {
            //CHECKSTYLE:ON
            jobExceptionHandler.handleException(jobName, cause);
        }
        execute(shardingContexts, shardingContexts.isFailover() ? JobExecutionEvent.ExecutionSource.FAILOVER : JobExecutionEvent.ExecutionSource.NORMAL_TRIGGER,executionType,shardingItems);
        while (jobFacade.isExecuteMisfired(shardingContexts.getShardingItemParameters().keySet())) {
            jobFacade.clearMisfire(shardingContexts.getShardingItemParameters().keySet());
            execute(shardingContexts, JobExecutionEvent.ExecutionSource.MISFIRE,executionType,shardingItems);
        }
        jobFacade.failoverIfNecessary();
        try {
            jobFacade.afterJobExecuted(shardingContexts);
            //CHECKSTYLE:OFF
        } catch (final Throwable cause) {
            //CHECKSTYLE:ON
            jobExceptionHandler.handleException(jobName, cause);
        }
    }
    
    private void execute(final ShardingContexts shardingContexts, final JobExecutionEvent.ExecutionSource executionSource ,ExecutionType executionType,String shardingItems) {
        if (shardingContexts.getShardingItemParameters().isEmpty()) {
            if (shardingContexts.isAllowSendJobEvent()) {
                JobStatusTraceEvent event = new JobStatusTraceEvent(jobName,shardingContexts.getTaskId(),executionType, null, JobStatusTraceEvent.State.TASK_FINISHED,String.format("Sharding item for job '%s' is empty.", jobName));
                jobFacade.postJobStatusTraceEvent(event);
            }
            return;
        }
        jobFacade.registerJobBegin(shardingContexts);
        if (shardingContexts.isAllowSendJobEvent()) {
            JobStatusTraceEvent event = new JobStatusTraceEvent(jobName,shardingContexts.getTaskId(),executionType, shardingItems, JobStatusTraceEvent.State.TASK_RUNNING,String.format("Sharding item for job '%s' is running.", jobName));
            jobFacade.postJobStatusTraceEvent(event);
        }
        try {
            process(shardingContexts, executionSource);
        } finally {
            // TODO 考虑增加作业失败的状态，并且考虑如何处理作业失败的整体回路
            jobFacade.registerJobCompleted(shardingContexts);
            if (!error) {
                if (shardingContexts.isAllowSendJobEvent()) {
                    JobStatusTraceEvent event = new JobStatusTraceEvent(jobName,shardingContexts.getTaskId(),executionType, shardingItems, JobStatusTraceEvent.State.TASK_FINISHED,String.format("Sharding item for job '%s' success.", jobName));
                    jobFacade.postJobStatusTraceEvent(event);
                }
            } else {
                if (shardingContexts.isAllowSendJobEvent()) {
                    JobStatusTraceEvent event = new JobStatusTraceEvent(jobName,shardingContexts.getTaskId(),executionType, shardingItems, JobStatusTraceEvent.State.TASK_ERROR,String.format("Sharding item for job '%s' is error", jobName));
                    jobFacade.postJobStatusTraceEvent(event);
                }
            }
        }
    }
    
    private void process(final ShardingContexts shardingContexts, final JobExecutionEvent.ExecutionSource executionSource) {
        Collection<Integer> items = shardingContexts.getShardingItemParameters().keySet();
        if (1 == items.size()) {
            int item = shardingContexts.getShardingItemParameters().keySet().iterator().next();
            JobExecutionEvent jobExecutionEvent =  new JobExecutionEvent(shardingContexts.getItemTaskId(item).get(), jobName, executionSource, item);
            process(shardingContexts, item, jobExecutionEvent);
            return;
        }
        final CountDownLatch latch = new CountDownLatch(items.size());
        for (final int each : items) {
            final JobExecutionEvent jobExecutionEvent = new JobExecutionEvent(shardingContexts.getItemTaskId(each).get(), jobName, executionSource, each);
            if (executorService.isShutdown()) {
                return;
            }
            executorService.submit(new Runnable() {
                
                @Override
                public void run() {
                    try {
                        process(shardingContexts, each, jobExecutionEvent);
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }
        try {
            latch.await();
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void process(final ShardingContexts shardingContexts, final int item, final JobExecutionEvent startEvent) {
        if (shardingContexts.isAllowSendJobEvent()) {
            jobFacade.postJobExecutionEvent(startEvent);
        }
        log.trace("Job '{}' executing, item is: '{}'.", jobName, item);
        JobExecutionEvent completeEvent;
        try {
            process(new ShardingContext(shardingContexts, item));
            completeEvent = startEvent.executionSuccess();
            log.trace("Job '{}' executed, item is: '{}'.", jobName, item);
            if (shardingContexts.isAllowSendJobEvent()) {
                jobFacade.postJobExecutionEvent(completeEvent);
            }
            error = false;
            // CHECKSTYLE:OFF
        } catch (final Throwable cause) {
            // CHECKSTYLE:ON
            error = true;
            completeEvent = startEvent.executionFailure(cause);
            jobFacade.postJobExecutionEvent(completeEvent);
            jobExceptionHandler.handleException(jobName, cause);
        }
    }
    
    protected abstract void process(ShardingContext shardingContext);
}
