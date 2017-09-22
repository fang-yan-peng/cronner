package cronner.jfaster.org.service;

import cronner.jfaster.org.pojo.ShardingInfo;

import java.util.Collection;

/**
 * @author fangyanpeng
 */
public interface DetailService {
    Collection<ShardingInfo> getDetail(String jobName);
}
