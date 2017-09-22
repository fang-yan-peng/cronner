package cronner.jfaster.org.job.api.script;

import cronner.jfaster.org.job.api.CronnerJob;

/**
 * 脚本分布式作业接口.
 * @author fangyanpeng
 */
public interface ScriptJob extends CronnerJob {
    String getCommandLine();
}
