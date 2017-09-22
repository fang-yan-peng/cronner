package cronner.jfaster.org.job.schedule;

import cronner.jfaster.org.job.election.LeaderNode;

/**
 * 调度节点
 * @author fangyanpeng
 */
public class ScheduleNode {

    public static final String ROOT = "schedule";

    public static final String LEADER_ROOT = LeaderNode.ROOT + "/" + ROOT;

    public static final String NECESSARY = LEADER_ROOT + "/necessary";

    public static final String SCHEDULING = LEADER_ROOT + "/scheduling";

}
