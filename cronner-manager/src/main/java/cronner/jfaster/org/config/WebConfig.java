package cronner.jfaster.org.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @author fangyanpeng
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingGsonHttpMessageConverter converter = new MappingGsonHttpMessageConverter();
        converters.add(0,converter);
    }

}