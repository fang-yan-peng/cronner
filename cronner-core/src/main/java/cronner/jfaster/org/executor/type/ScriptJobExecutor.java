package cronner.jfaster.org.executor.type;

import com.google.common.base.Strings;
import cronner.jfaster.org.exeception.JobConfigurationException;
import cronner.jfaster.org.executor.AbstractCronnerJobExecutor;
import cronner.jfaster.org.executor.JobFacade;
import cronner.jfaster.org.job.api.ShardingContext;
import cronner.jfaster.org.job.api.script.ScriptJob;
import cronner.jfaster.org.util.json.GsonFactory;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import java.io.IOException;

/**
 * 脚本作业执行器.
 *
 * @author fangyanpeng
 */
public final class ScriptJobExecutor extends AbstractCronnerJobExecutor {

    private final ScriptJob scriptJob;

    public ScriptJobExecutor(final JobFacade jobFacade, ScriptJob scriptJob) {
        super(jobFacade);
        this.scriptJob = scriptJob;
    }
    
    @Override
    protected void process(final ShardingContext shardingContext) {
        final String scriptCommandLine = scriptJob.getCommandLine();
        if (Strings.isNullOrEmpty(scriptCommandLine)) {
            throw new JobConfigurationException("Cannot find script command line for job '%s', job is not executed.", shardingContext.getJobName());
        }
        executeScript(shardingContext, scriptCommandLine);
    }
    
    private void executeScript(final ShardingContext shardingContext, final String scriptCommandLine) {
        CommandLine commandLine = CommandLine.parse(scriptCommandLine);
        commandLine.addArgument(GsonFactory.getGson().toJson(shardingContext), false);
        try {
            new DefaultExecutor().execute(commandLine);
        } catch (final IOException ex) {
            throw new JobConfigurationException("Execute script failure.", ex);
        }
    }
}
