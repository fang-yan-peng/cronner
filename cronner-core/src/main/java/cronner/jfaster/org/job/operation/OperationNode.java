package cronner.jfaster.org.job.operation;

/**
 *
 * 作业操作节点
 * @author fangyanpeng
 */
public class OperationNode {

    public final static String OPERATION ="operation";

    private final String jobName;

    public OperationNode(final String jobName) {
        this.jobName = jobName;
    }

    public String getOperationNodePath() {
        return String.format("/%s/%s", jobName, OPERATION);
    }

    public boolean isOperationPath(final String path) {
        return getOperationNodePath().equals(path);
    }

}
