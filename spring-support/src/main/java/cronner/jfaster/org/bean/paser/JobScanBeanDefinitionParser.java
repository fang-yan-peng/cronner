package cronner.jfaster.org.bean.paser;

import com.google.common.base.Strings;
import cronner.jfaster.org.ExecutorInitializer;
import cronner.jfaster.org.ExecutorRestfulBootstrap;
import cronner.jfaster.org.job.annotation.Job;
import cronner.jfaster.org.exeception.JobConfigurationException;
import cronner.jfaster.org.job.api.dataflow.DataflowJob;
import cronner.jfaster.org.job.api.listener.DefaultCronnerJobListener;
import cronner.jfaster.org.job.api.script.ScriptJob;
import cronner.jfaster.org.job.api.simple.SimpleJob;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.w3c.dom.Element;

import java.util.List;

import static cronner.jfaster.org.constants.CronnerConstant.ZK_NAME;


/**
 *
 * 扫描作业转换器
 *
 * @author fangyanpeng
 */
public class JobScanBeanDefinitionParser extends AbstractScanBeanParser{

    @Override
    public void registerCandidateComponents(Element element, String basePackage, ParserContext parserContext) {
        try {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    resolveBasePackage(basePackage,parserContext.getDelegate().getEnvironment()) + "/" + this.resourcePattern;
            Resource[] rs = resourcePatternResolver.getResources(packageSearchPath);
            for (Resource r : rs) {
                MetadataReader reader = metadataReaderFactory.getMetadataReader(r);
                AnnotationMetadata annotationMD = reader.getAnnotationMetadata();
                if (annotationMD.hasAnnotation(Job.class.getName())) {
                    ClassMetadata clazzMD = reader.getClassMetadata();
                    Class<?> jobClass = Class.forName(clazzMD.getClassName());
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
                    factory.addConstructorArgValue(createJobListener(jobAnnotation));
                    parserContext.getRegistry().registerBeanDefinition(name,factory.getBeanDefinition());

                }
            }
        } catch (Exception e) {
            throw new JobConfigurationException(e);
        }
    }

    private List<BeanDefinition> createJobListener(Job job) {
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
