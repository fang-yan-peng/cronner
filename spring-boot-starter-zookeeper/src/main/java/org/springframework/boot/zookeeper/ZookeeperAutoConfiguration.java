package org.springframework.boot.zookeeper;

import cronner.jfaster.org.constants.CronnerConstant;
import cronner.jfaster.org.registry.base.CoordinatorRegistryCenter;
import cronner.jfaster.org.registry.zookeeper.ZookeeperConfiguration;
import cronner.jfaster.org.registry.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fangyanpeng
 */
@Configuration
@ConditionalOnClass({ZookeeperRegistryCenter.class})
public class ZookeeperAutoConfiguration {

    @Bean(name = CronnerConstant.ZK_NAME)
    @ConditionalOnMissingBean(ZookeeperRegistryCenter.class)
    public CoordinatorRegistryCenter autoConfigZk(@Value("${zookeeper.serverLists}") String serverLists,
                                                  @Value("${zookeeper.namespace}") String namespace,
                                                  @Value("${zookeeper.baseSleepTimeMilliseconds}") int baseSleepTimeMilliseconds,
                                                  @Value("${zookeeper.maxSleepTimeMilliseconds}") int maxSleepTimeMilliseconds,
                                                  @Value("${zookeeper.maxRetries}") int maxRetries,
                                                  @Value("${zookeeper.connectionTimeoutMilliseconds:0}") int connectionTimeoutMilliseconds,
                                                  @Value("${zookeeper.sessionTimeoutMilliseconds:0}") int sessionTimeoutMilliseconds,
                                                  @Value("${zookeeper.digest:default}") String dist){
        ZookeeperConfiguration configuration = new ZookeeperConfiguration(serverLists,namespace);
        configuration.setBaseSleepTimeMilliseconds(baseSleepTimeMilliseconds);
        configuration.setMaxSleepTimeMilliseconds(maxSleepTimeMilliseconds);
        configuration.setMaxRetries(maxRetries);
        configuration.setConnectionTimeoutMilliseconds(connectionTimeoutMilliseconds);
        configuration.setSessionTimeoutMilliseconds(sessionTimeoutMilliseconds);
        dist = "default".equals(dist) ? "" : dist;
        configuration.setDigest(dist);
        ZookeeperRegistryCenter registryCenter = new ZookeeperRegistryCenter(configuration);
        registryCenter.init();
        return registryCenter;

    }
}
