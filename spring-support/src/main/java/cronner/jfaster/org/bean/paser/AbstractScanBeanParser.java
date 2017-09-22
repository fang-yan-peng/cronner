package cronner.jfaster.org.bean.paser;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import static cronner.jfaster.org.bean.BeanConstant.DEFAULT_RESOURCE_PATTERN;
import static cronner.jfaster.org.bean.BeanConstant.PACKAGE;

/**
 * @author fangyanpeng
 *
 */
public abstract class AbstractScanBeanParser implements BeanDefinitionParser {

    protected String resourcePattern = DEFAULT_RESOURCE_PATTERN;

    protected static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    protected static MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        String[] basePackages = StringUtils.tokenizeToStringArray(element.getAttribute(PACKAGE), ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
        for(String basePackage : basePackages){
            registerCandidateComponents(element,basePackage,parserContext);
        }
        return null;
    }

    protected String resolveBasePackage(String basePackage,Environment environment) {
        return ClassUtils.convertClassNameToResourcePath(environment.resolveRequiredPlaceholders(basePackage));
    }
    public abstract void registerCandidateComponents(Element element, String basePackage,ParserContext parserContext);
}
