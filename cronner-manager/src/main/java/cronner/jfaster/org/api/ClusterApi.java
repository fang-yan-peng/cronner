package cronner.jfaster.org.api;

import cronner.jfaster.org.executor.JobCompleteHandler;
import cronner.jfaster.org.job.schedule.JobRegistry;
import cronner.jfaster.org.job.schedule.JobScheduler;
import cronner.jfaster.org.model.JobConfiguration;
import cronner.jfaster.org.model.JsonResponse;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;
import cronner.jfaster.org.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 调度节点集群操作
 *
 * @author fangyanpeng
 */
@Slf4j
@RestController
@RequestMapping("/cluster")
public class ClusterApi {

    @Resource
    private TaskService taskService;

    @Resource
    private CoordinatorRegistryCenter registryCenter;

    @Resource
    private JobCompleteHandler handler;

    @Value("${server.port}")
    private int serverPort;

    @RequestMapping(value = "/add",method = RequestMethod.PUT)
    public JsonResponse addJob(@RequestBody final JobConfiguration configuration){
        try {
            if(JobRegistry.getInstance().getJobInstance(configuration.getJobName()) == null) {
                JobScheduler jobScheduler = new JobScheduler(registryCenter, configuration, serverPort, taskService,handler);
                jobScheduler.init();
            }
        } catch (Exception e) {
            log.error("Leader add job error: ",e);
            JsonResponse.notOk(e.getMessage());
        }
        return JsonResponse.ok();
    }

}
