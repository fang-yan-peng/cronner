package org.springframework.boot.cronner;

import com.google.common.base.Strings;
import cronner.jfaster.org.ExecutorInitializer;
import cronner.jfaster.org.ExecutorRestfulBootstrap;
import cronner.jfaster.org.job.annotation.Job;
import cronner.jfaster.org.exeception.JobConfigurationException;
import cronner.jfaster.org.job.api.dataflow.DataflowJob;
import cronner.jfaster.org.job.api.listener.DefaultCronnerJobListener;
import cronner.jfaster.org.job.api.script.ScriptJob;
import cronner.jfaster.org.job.api.simple.SimpleJob;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static cronner.jfaster.org.constants.CronnerConstant.ZK_NAME;

/**
 * @author fangyanpeng
 *
 * 根据注解注入job到spring
 *
 */
public class CronnerBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    private final String basePacke;

    public CronnerBeanFactoryPostProcessor(String basePacke) {
        this.basePacke = basePacke;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            String[] basePackages = StringUtils.tokenizeToStringArray(basePacke, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
            for(String basePackage : basePackages){
                String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + basePackage.replaceAll("\\.", "/") + "/**/*.class";
                Resource[] rs = resourcePatternResolver.getResources(packageSearchPath);
                for (Resource r : rs) {
                    MetadataReader reader = metadataReaderFactory.getMetadataReader(r);
                    AnnotationMetadata annotationMD = reader.getAnnotationMetadata();
                    if (annotationMD.hasAnnotation(Job.class.getName())) {
                        ClassMetadata clazzMD = reader.getClassMetadata();
                        //Class<?> jobClass = Class.forName(clazzMD.getClassName());
                        Class<?> jobClass = ClassUtils.getDefaultClassLoader().loadClass(clazzMD.getClassName());
                        if(!DataflowJob.class.isAssignableFrom(jobClass) && !SimpleJob.class.isAssignableFrom(jobClass) && !ScriptJob.class.isAssignableFrom(jobClass)){
                            throw new JobConfigurationException("registried job should not be a implement of DataflowJob, SimpleJob or ScriptJob");
                        }
                        Job jobAnnotation = jobClass.getAnnotation(Job.class);
                        String name = jobAnnotation.name();
                        if(Strings.isNullOrEmpty(name)){
                            throw new JobConfigurationException("Attribute 'name' is not allowed to be null");
                        }
                        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(ExecutorInitializer.class);
                        factory.setInitMethodName("init");
                        factory.addConstructorArgReference(ZK_NAME);
                        factory.addConstructorArgValue(BeanDefinitionBuilder.rootBeanDefinition(jobClass).getBeanDefinition());
                        factory.addConstructorArgValue(name);
                        factory.addConstructorArgValue(ExecutorRestfulBootstrap.start());
                        factory.addConstructorArgValue(createJobListeners(jobAnnotation));
                        ((DefaultListableBeanFactory) beanFactory).registerBeanDefinition(name,factory.getBeanDefinition());

                    }
                }
            }
        } catch (Exception e) {
            throw new JobConfigurationException(e);
        }
    }

    private List<BeanDefinition> createJobListeners(Job job) {
        List<BeanDefinition> result = null;
        if (job.listener() != DefaultCronnerJobListener.class) {
            result = new ManagedList<>();
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(job.listener());
            factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
            result.add(factory.getBeanDefinition());
        }
        return result;
    }
}
