package cronner.jfaster.org.job.server;


import cronner.jfaster.org.job.schedule.JobRegistry;
import cronner.jfaster.org.job.storage.JobNodePath;
import cronner.jfaster.org.util.IpUtils;

import java.util.regex.Pattern;

/**
 * 服务器节点路径.
 * @author fangyanpeng
 */
public final class ServerNode {
    
    /**
     * 服务器信息根节点.
     */
    public static final String ROOT = "servers";
    
    private static final String SERVERS = ROOT + "/%s";
    
    private final String jobName;
    
    private final JobNodePath jobNodePath;
    
    public ServerNode(final String jobName) {
        this.jobName = jobName;
        jobNodePath = new JobNodePath(jobName);
    }
    
    /**
     * 判断给定路径是否为作业服务器路径.
     *
     * @param path 待判断的路径
     * @return 是否为作业服务器路径
     */
    public boolean isServerPath(final String path) {
        return Pattern.compile(jobNodePath.getFullPath(ServerNode.ROOT) + "/" + IpUtils.IP_REGEX).matcher(path).matches();
    }
    
    /**
     * 判断给定路径是否为本地作业服务器路径.
     *
     * @param path 待判断的路径
     * @return 是否为本地作业服务器路径
     */
    public boolean isLocalServerPath(final String path) {
        return path.equals(jobNodePath.getFullPath(String.format(SERVERS, JobRegistry.getInstance().getJobInstance(jobName).getIp())));
    }
    
    String getServerNode(final String ip) {
        return String.format(SERVERS, ip);
    }
}
