package cronner.jfaster.org.name;

import cronner.jfaster.org.bean.paser.JobBeanDefinitionParser;
import cronner.jfaster.org.bean.paser.JobScanBeanDefinitionParser;
import cronner.jfaster.org.bean.paser.ZookeeperBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * cronner 命名空间处理器
 * @author fangyanpeng
 */
public class CronnerNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("zookeeper",new ZookeeperBeanDefinitionParser());
        registerBeanDefinitionParser("job", new JobBeanDefinitionParser());
        registerBeanDefinitionParser("job-scan",new JobScanBeanDefinitionParser());
    }
}
