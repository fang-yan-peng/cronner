package cronner.jfaster.org.job.config;


import cronner.jfaster.org.job.storage.JobNodePath;

/**
 * 配置节点路径.
 * @author fangyanpeng
 */
public final class ConfigurationNode {
    
    static final String ROOT = "config";
    
    private final JobNodePath jobNodePath;
    
    public ConfigurationNode(final String jobName) {
        jobNodePath = new JobNodePath(jobName);
    }
    
    /**
     * 判断是否为作业配置根路径.
     * 
     * @param path 节点路径
     * @return 是否为作业配置根路径
     */
    public boolean isConfigPath(final String path) {
        return jobNodePath.getConfigNodePath().equals(path);
    }
}
