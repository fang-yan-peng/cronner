package cronner.jfaster.org.job.config;

import cronner.jfaster.org.model.JobConfiguration;
import cronner.jfaster.org.util.json.GsonFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


/**
 * 作业配置的Gson工厂.
 * @author fangyanpeng
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JobConfigurationGsonFactory {
    
    /**
     * 将作业配置转换为JSON字符串.
     * 
     * @param jobConfig 作业配置对象
     * @return 作业配置JSON字符串
     */
    public static String toJson(final JobConfiguration jobConfig) {
        return GsonFactory.getGson().toJson(jobConfig);
    }
    
    /**
     * 将作业配置转换为JSON字符串.
     *
     * @param jobConfig 作业配置对象
     * @return 作业配置JSON字符串
     */
    public static String toJsonForObject(final Object jobConfig) {
        return GsonFactory.getGson().toJson(jobConfig);
    }
    
    /**
     * 将JSON字符串转换为作业配置.
     *
     * @param jobConfigJson 作业配置JSON字符串
     * @return 作业配置对象
     */
    public static JobConfiguration fromJson(final String jobConfigJson) {
        return GsonFactory.getGson().fromJson(jobConfigJson, JobConfiguration.class);
    }
}
