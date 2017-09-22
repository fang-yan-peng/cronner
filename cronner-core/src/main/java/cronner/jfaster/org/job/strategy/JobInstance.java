package cronner.jfaster.org.job.strategy;

import cronner.jfaster.org.util.IpUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * 作业运行实例.
 * @author fangyanpeng
 */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(of = "jobInstanceId")
public final class JobInstance {
    
    private static final String DELIMITER = ":";

    /**
     * 作业实例主键.
     */
    private final String jobInstanceId;


    public JobInstance(int port) {
        jobInstanceId = IpUtils.getIp() + DELIMITER + port;
    }

    
    /**
     * 获取作业服务器IP地址.
     * 
     * @return 作业服务器IP地址
     */
    public String getIp() {
        return jobInstanceId.substring(0, jobInstanceId.indexOf(DELIMITER));
    }
}
