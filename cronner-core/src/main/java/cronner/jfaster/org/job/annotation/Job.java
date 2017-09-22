package cronner.jfaster.org.job.annotation;

import cronner.jfaster.org.job.api.listener.CronnerJobListener;
import cronner.jfaster.org.job.api.listener.DefaultCronnerJobListener;

import java.lang.annotation.*;

/**
 *
 * cronner job 扫描标识
 *
 * @author fangyanpeng
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Job {

    String name() default "";

    Class<? extends CronnerJobListener> listener() default DefaultCronnerJobListener.class;

}
