package cronner.jfaster.org.api;

import cronner.jfaster.org.executor.AbstractCronnerJobExecutor;
import cronner.jfaster.org.executor.ShardingContexts;
import cronner.jfaster.org.model.JsonResponse;
import cronner.jfaster.org.executor.store.JobExecutorRegistry;
import cronner.jfaster.org.util.executor.ExecuteThreadService;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * 接受调度节点的调度请求
 * @author fangyanpeng
 */
@Slf4j
@Path("/execute")
public class ExecuteApi {

    @PUT
    @Path("/trigger")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public JsonResponse findJobExecutionEvents(final ShardingContexts shardingContexts) {
        String jobName = shardingContexts.getJobName();
        final AbstractCronnerJobExecutor executor = JobExecutorRegistry.getInstance().getJobExecutor(jobName);
        if(executor == null){
            return JsonResponse.notOk("Executor is not registered");
        }
        //异步执行，执行结果通过通知的方式回告
        ExecuteThreadService.sumbmit(new Runnable() {
            @Override
            public void run() {
                executor.execute(shardingContexts);
            }
        });
        return JsonResponse.ok();
    }

}
