package org.springframework.boot.mango;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.jfaster.mango.plugin.spring.MangoDaoScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.mango.exeception.MangoSpringBootException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

import java.util.List;

/**
 * @author fangyanpeng
 */
@Configuration
@ConditionalOnClass({MangoDaoScanner.class})
public class MangoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(MangoDaoScanner.class)
    public MangoDaoScanner autoScanner(@Value("${mango.scanPackage}") String scanPackage, @Value("${mango.factoryClass}") String factoryClass){
        MangoDaoScanner daoScanner = new MangoDaoScanner();
        List<String> packages = Splitter.on(",").splitToList(scanPackage);
        daoScanner.setPackages(packages);
        if(!Strings.isNullOrEmpty(factoryClass)){
            //判断是否定制factoryClass，默认是：DefaultMangoFactoryBean（从spring中获取Mango实例）。
            try {
                daoScanner.setFactoryBeanClass(ClassUtils.forName(factoryClass, MangoAutoConfiguration.class.getClassLoader()));
            } catch (ClassNotFoundException e) {
                throw new MangoSpringBootException(e);
            }
        }
        return daoScanner;
    }
}
