package cronner.jfaster.org.job.instance;

/**
 *
 * 执行任务节点
 * @author fangyanpeng
 */
public class ExecuteInstanceNode extends AbstractInstanceNode {

    public ExecuteInstanceNode(String jobName) {
        super(jobName);
    }

    @Override
    public String getRoot() {
        return EXECUTE_ROOT;
    }
}
