package cronner.jfaster.org.job.schedule;

import com.google.common.base.Strings;
import cronner.jfaster.org.event.JobEventBus;
import cronner.jfaster.org.event.type.JobExecutionEvent;
import cronner.jfaster.org.event.type.JobStatusTraceEvent;
import cronner.jfaster.org.executor.JobFacade;
import cronner.jfaster.org.executor.ShardingContexts;
import cronner.jfaster.org.job.api.listener.CronnerJobListener;
import cronner.jfaster.org.job.config.ConfigurationService;
import cronner.jfaster.org.job.failover.FailoverService;
import cronner.jfaster.org.job.sharding.ExecutionService;
import cronner.jfaster.org.job.sharding.ShardingService;
import cronner.jfaster.org.model.JobConfiguration;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
/**
 * 为作业提供内部服务的门面类.
 * @author fangyanpeng
 */
@Slf4j
public final class CronnerJobFacade implements JobFacade {
    
    private final ConfigurationService configService;
    
    private final ShardingService shardingService;

    private final ExecutionService executionService;
    
    private final FailoverService failoverService;
    
    private final List<CronnerJobListener> cronnerJobListeners;
    
    private final JobEventBus jobEventBus;

    private final String jobName;
    
    public CronnerJobFacade(final CoordinatorRegistryCenter regCenter, final String jobName, final List<CronnerJobListener> cronnerJobListeners, final JobEventBus jobEventBus) {
        this.jobName = jobName;
        configService = new ConfigurationService(regCenter, jobName);
        shardingService = new ShardingService(regCenter, jobName);
        executionService = new ExecutionService(regCenter, jobName);
        failoverService = new FailoverService(regCenter, jobName);
        this.cronnerJobListeners = cronnerJobListeners;
        this.jobEventBus = jobEventBus;
    }
    
    @Override
    public JobConfiguration loadJobRootConfiguration(final boolean fromCache) {
        return configService.load(fromCache);
    }
    
    @Override
    public void failoverIfNecessary() {
        if (configService.load(true).isFailover()) {
            failoverService.failoverIfNecessary();
        }
    }
    
    @Override
    public void registerJobBegin(final ShardingContexts shardingContexts) {
        executionService.registerJobBegin(shardingContexts);
    }
    
    @Override
    public void registerJobCompleted(final ShardingContexts shardingContexts) {
        executionService.registerJobCompleted(shardingContexts);
        if (configService.load(true).isFailover()) {
            failoverService.updateFailoverComplete(shardingContexts.getShardingItemParameters().keySet());
        }
    }

    @Override
    public boolean misfireIfRunning(final Collection<Integer> shardingItems) {
        return executionService.misfireIfHasRunningItems(shardingItems);
    }
    
    @Override
    public void clearMisfire(final Collection<Integer> shardingItems) {
        executionService.clearMisfire(shardingItems);
    }
    
    @Override
    public boolean isExecuteMisfired(final Collection<Integer> shardingItems) {
        return isEligibleForJobRunning() && configService.load(true).isMisfire() && !executionService.getMisfiredJobItems(shardingItems).isEmpty();
    }
    
    @Override
    public boolean isEligibleForJobRunning() {
        JobConfiguration jobConfig = configService.load(true);
        if (jobConfig.getType() == 1) {
            return !shardingService.isNeedSharding() && jobConfig.isStreamingProcess();
        }
        return !shardingService.isNeedSharding();
    }
    
    @Override
    public boolean isNeedSharding() {
        return shardingService.isNeedSharding();
    }
    
    @Override
    public void beforeJobExecuted(final ShardingContexts shardingContexts) {
        for (CronnerJobListener each : cronnerJobListeners) {
            each.beforeJobExecuted(shardingContexts);
        }
    }
    
    @Override
    public void afterJobExecuted(final ShardingContexts shardingContexts) {
        for (CronnerJobListener each : cronnerJobListeners) {
            each.afterJobExecuted(shardingContexts);
        }
    }
    
    @Override
    public void postJobExecutionEvent(final JobExecutionEvent jobExecutionEvent) {
        jobEventBus.post(jobExecutionEvent);
    }
    
    @Override
    public void postJobStatusTraceEvent(JobStatusTraceEvent jobStatusTraceEvent) {
        jobEventBus.post(jobStatusTraceEvent);
        if (!Strings.isNullOrEmpty(jobStatusTraceEvent.getMessage())) {
            log.trace(jobStatusTraceEvent.getMessage());
        }
    }

    @Override
    public String getJobName() {
        return jobName;
    }
}
