package cronner.jfaster.org.service.impl;

import cronner.jfaster.org.job.server.ServerStatus;
import cronner.jfaster.org.job.storage.JobNodePath;
import cronner.jfaster.org.pojo.ShardingInfo;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;
import cronner.jfaster.org.service.DetailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 *
 * @author fangyanpeng
 */
@Service
public class DetailServiceImpl implements DetailService {

    @Resource
    private CoordinatorRegistryCenter regCenter;

    @Override
    public Collection<ShardingInfo> getDetail(String jobName) {
        JobNodePath jobNodePath = new JobNodePath(jobName);
        String shardingRootPath = jobNodePath.getShardingNodePath();
        if(!regCenter.isExisted(shardingRootPath)){
            return Collections.EMPTY_LIST;
        }
        String instanceRootPath = jobNodePath.getInstancesNodePath();
        List<String> instances = regCenter.getChildrenKeys(instanceRootPath);
        List<String> items = regCenter.getChildrenKeys(shardingRootPath);
        List<ShardingInfo> result = new ArrayList<>(items.size());
        for (String each : items) {
            result.add(getShardingInfo(jobName, each,instances));
        }
        if(!instances.isEmpty()){
            for (String instance : instances){
                ShardingInfo info = new ShardingInfo();
                info.setFailover(false);
                String[] ipAndPid = instance.split(":");
                info.setServerIp(ipAndPid[0]);
                info.setPort(ipAndPid[1]);
                info.setItem(-1);
                if(ServerStatus.DISABLED.name().equals(regCenter.get(jobNodePath.getInstanceNodePath(instance)))){
                    info.setStatus(ShardingInfo.ShardingStatus.DISABLED);
                }else {
                    info.setStatus(ShardingInfo.ShardingStatus.SHARDING_FLAG);
                }
                result.add(info);
            }
        }
        Collections.sort(result);
        return result;
    }

    private ShardingInfo getShardingInfo(final String jobName, final String item,List<String> instances) {
        ShardingInfo result = new ShardingInfo();
        result.setItem(Integer.parseInt(item));
        JobNodePath jobNodePath = new JobNodePath(jobName);
        String instanceId = regCenter.get(jobNodePath.getShardingNodePath(item, "instance"));
        instances.remove(instanceId);
        boolean running = regCenter.isExisted(jobNodePath.getShardingNodePath(item, "running"));
        boolean shardingError = !regCenter.isExisted(jobNodePath.getInstanceNodePath(instanceId));
        boolean disabled = !shardingError ? ServerStatus.DISABLED.name().equals(regCenter.get(jobNodePath.getInstanceNodePath(instanceId))) : true;
        result.setStatus(ShardingInfo.ShardingStatus.getShardingStatus(disabled, running, shardingError));
        result.setFailover(regCenter.isExisted(jobNodePath.getShardingNodePath(item, "failover")));
        if (null != instanceId) {
            String[] ipAndPid = instanceId.split(":");
            result.setServerIp(ipAndPid[0]);
            result.setPort(ipAndPid[1]);
        }
        return result;
    }
}
