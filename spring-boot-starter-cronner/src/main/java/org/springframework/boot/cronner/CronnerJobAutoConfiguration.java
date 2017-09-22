package org.springframework.boot.cronner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author fangyanpeng
 *
 * 自动注册扫描job
 *
 */
@Configuration
@ConditionalOnClass({CronnerBeanFactoryPostProcessor.class})
public class CronnerJobAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(CronnerBeanFactoryPostProcessor.class)
    public CronnerBeanFactoryPostProcessor autoScanner(@Value("${cronner.package}") String scanPackage){
        CronnerBeanFactoryPostProcessor cronnerRgistryJob = new CronnerBeanFactoryPostProcessor(scanPackage);
        return cronnerRgistryJob;
    }
}
