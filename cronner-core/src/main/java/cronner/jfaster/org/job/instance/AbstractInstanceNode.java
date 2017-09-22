package cronner.jfaster.org.job.instance;

import cronner.jfaster.org.job.schedule.JobRegistry;
import cronner.jfaster.org.job.storage.JobNodePath;

/**
 * 实例节点路径.
 * @author fangyanpeng
 */
public abstract class AbstractInstanceNode {

    public final static String SCHEDULE_ROOT="schedule_instances";

    public final static String EXECUTE_ROOT = "instances";

    /**
     * 运行实例信息根节点.
     */
    private final String INSTANCES = getRoot() + "/%s";
    
    private final String jobName;
    
    private final JobNodePath jobNodePath;
    
    public AbstractInstanceNode(final String jobName) {
        this.jobName = jobName;
        jobNodePath = new JobNodePath(jobName);
    }

    public abstract String getRoot();

    /**
     * 获取作业运行实例全路径.
     *
     * @return 作业运行实例全路径
     */
    public String getInstanceFullPath() {
        return jobNodePath.getFullPath(getRoot());
    }
    
    /**
     * 判断给定路径是否为作业运行实例路径.
     *
     * @param path 待判断的路径
     * @return 是否为作业运行实例路径
     */
    public boolean isInstancePath(final String path) {
        return path.startsWith(jobNodePath.getFullPath(getRoot()));
    }

    boolean isLocalInstancePath(final String path) {
        return path.equals(jobNodePath.getFullPath(String.format(INSTANCES, JobRegistry.getInstance().getJobInstance(jobName).getJobInstanceId())));
    }
    
    public String getLocalInstanceNode() {
        return String.format(INSTANCES, JobRegistry.getInstance().getJobInstance(jobName).getJobInstanceId());
    }

    public String getInstanceNode(String jobInstanceId) {
        return String.format(INSTANCES, jobInstanceId);
    }
}
