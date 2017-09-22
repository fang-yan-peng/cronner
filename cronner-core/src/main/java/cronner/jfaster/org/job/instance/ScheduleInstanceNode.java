package cronner.jfaster.org.job.instance;

/**
 *
 * 调度实例节点
 *
 * @author fangyanpeng
 */
public class ScheduleInstanceNode extends AbstractInstanceNode{

    public ScheduleInstanceNode(String jobName) {
        super(jobName);
    }

    @Override
    public String getRoot() {
        return SCHEDULE_ROOT;
    }
}
