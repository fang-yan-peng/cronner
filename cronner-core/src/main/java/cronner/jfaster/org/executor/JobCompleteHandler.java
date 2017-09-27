package cronner.jfaster.org.executor;

/**
 * @author fangyanpeng
 */
public interface JobCompleteHandler {
    void complete(String jobName,final boolean fail);
}
