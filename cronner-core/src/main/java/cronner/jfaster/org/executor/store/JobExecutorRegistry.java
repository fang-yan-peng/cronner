package cronner.jfaster.org.executor.store;

import cronner.jfaster.org.exeception.JobConfigurationException;
import cronner.jfaster.org.executor.AbstractCronnerJobExecutor;
import cronner.jfaster.org.executor.JobFacade;
import cronner.jfaster.org.executor.type.DataflowJobExecutor;
import cronner.jfaster.org.executor.type.ScriptJobExecutor;
import cronner.jfaster.org.executor.type.SimpleJobExecutor;
import cronner.jfaster.org.job.api.CronnerJob;
import cronner.jfaster.org.job.api.dataflow.DataflowJob;
import cronner.jfaster.org.job.api.script.ScriptJob;
import cronner.jfaster.org.job.api.simple.SimpleJob;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 作业执行注册中心
 *
 * @author fangyanpeng
 *
 */
public class JobExecutorRegistry {

    private static final Map<String,AbstractCronnerJobExecutor> executors = new ConcurrentHashMap<>();

    private static volatile JobExecutorRegistry instance;

    /**
     * 获取执行作业注册表实例.
     *
     * @return 执行作业注册实例
     */
    public static JobExecutorRegistry getInstance() {
        if (null == instance) {
            synchronized (JobExecutorRegistry.class) {
                if (null == instance) {
                    instance = new JobExecutorRegistry();
                }
            }
        }
        return instance;
    }

    /**
     * 添加作业调度控制器.
     *
     * @param jobName 作业名称
     * @param cronnerJob 执行作业实例
     * @param jobFacade 执行作业门面
     */
    public void registerJobExecutor(final String jobName, final CronnerJob cronnerJob , JobFacade jobFacade) {
        if (cronnerJob instanceof ScriptJob) {
            executors.put(jobName,new ScriptJobExecutor(jobFacade, (ScriptJob) cronnerJob));
        }else if (cronnerJob instanceof SimpleJob) {
            executors.put(jobName,new SimpleJobExecutor((SimpleJob) cronnerJob, jobFacade));
        }else if (cronnerJob instanceof DataflowJob) {
            executors.put(jobName,new DataflowJobExecutor((DataflowJob) cronnerJob, jobFacade));
        }else {
            throw new JobConfigurationException("Cannot support job type '%s'", cronnerJob.getClass().getCanonicalName());
        }
    }

    /**
     * 根据作业名称获取执行实例
     * @param jobName
     * @return 执行实例
     */
    public AbstractCronnerJobExecutor getJobExecutor(final String jobName){
        return executors.get(jobName);
    }
}
