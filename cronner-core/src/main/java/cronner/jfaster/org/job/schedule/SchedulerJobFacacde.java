package cronner.jfaster.org.job.schedule;

import cronner.jfaster.org.executor.ScheduleJobFacade;
import cronner.jfaster.org.executor.ShardingContexts;
import cronner.jfaster.org.job.sharding.ExecutionContextService;
import cronner.jfaster.org.job.sharding.ExecutionService;
import cronner.jfaster.org.job.sharding.ShardingService;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fangyanpeng
 */
public class SchedulerJobFacacde implements ScheduleJobFacade {

    private final ShardingService shardingService;

    private final ExecutionService executionService;

    private final ExecutionContextService executionContextService;

    private final String jobName;


    public SchedulerJobFacacde(CoordinatorRegistryCenter regCenter, String jobName){
        this.jobName = jobName;
        shardingService = new ShardingService(regCenter, jobName);
        executionService = new ExecutionService(regCenter,jobName);
        executionContextService = new ExecutionContextService(regCenter, jobName);
    }

    @Override
    public Map<String, ShardingContexts> scheduleShardingContexts() {
        shardingService.shardingIfNecessary();
        Map<String,List<Integer>> shardingItems= shardingService.getAllShardingItems();
        Map<String,ShardingContexts> shardingContextsMap = new HashMap<>(shardingItems.size());
        for (Map.Entry<String,List<Integer>> entry : shardingItems.entrySet()){
            List<Integer> items = entry.getValue();
            /*items.removeAll(executionService.getDisabledItems(items));*/
            shardingContextsMap.put(entry.getKey(),executionContextService.getJobShardingContext(items));
        }
        return shardingContextsMap;
    }

    @Override
    public String getJobName() {
        return jobName;
    }
}
