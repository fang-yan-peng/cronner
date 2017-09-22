package cronner.jfaster.org.bean.paser;

import com.google.common.base.Strings;
import cronner.jfaster.org.ExecutorInitializer;
import cronner.jfaster.org.ExecutorRestfulBootstrap;
import cronner.jfaster.org.exeception.JobConfigurationException;
import cronner.jfaster.org.job.api.script.ScriptJobImpl;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.List;

import static cronner.jfaster.org.bean.BeanConstant.*;
import static cronner.jfaster.org.constants.CronnerConstant.ZK_NAME;

/**
 *
 * 作业bean转换器
 * @author fangyanpeng
 *
 */
public class JobBeanDefinitionParser extends AbstractBeanDefinitionParser {

    @Override
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(ExecutorInitializer.class);
        factory.setInitMethodName("init");
        factory.addConstructorArgReference(ZK_NAME);
        if(!Strings.isNullOrEmpty(element.getAttribute(JOB_REF))){
            factory.addConstructorArgReference(element.getAttribute(JOB_REF));
        }else if(!Strings.isNullOrEmpty(element.getAttribute(CLASS_ATTRIBUTE))){
            factory.addConstructorArgValue(BeanDefinitionBuilder.rootBeanDefinition(element.getAttribute(CLASS_ATTRIBUTE)).getBeanDefinition());
        }else if(!Strings.isNullOrEmpty(element.getAttribute(COMMAND_LINE))){
            factory.addConstructorArgValue(new ScriptJobImpl(element.getAttribute(COMMAND_LINE)));
        }else {
            throw new JobConfigurationException(" One of 'job-ref' , 'class' and 'command-line' need to be configured");
        }
        factory.addConstructorArgValue(element.getAttribute(NAME));
        factory.addConstructorArgValue(ExecutorRestfulBootstrap.start());
        factory.addConstructorArgValue(createJobListener(element));
        return factory.getBeanDefinition();
    }

    private List<BeanDefinition> createJobListener(final Element element) {
        Element listenerElement = DomUtils.getChildElementByTagName(element, LISTENER);
        List<BeanDefinition> result = null;
        if (null != listenerElement) {
            result = new ManagedList<>();
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(listenerElement.getAttribute(CLASS_ATTRIBUTE));
            factory.setScope(BeanDefinition.SCOPE_PROTOTYPE);
            result.add(factory.getBeanDefinition());
        }
        return result;
    }

    @Override
    protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext) throws BeanDefinitionStoreException {
        String id = element.getAttribute("name");
        if(!StringUtils.hasText(id) && this.shouldGenerateIdAsFallback()) {
            id = parserContext.getReaderContext().generateBeanName(definition);
        }
        return id;
    }
}
