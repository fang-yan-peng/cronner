package cronner.jfaster.org.job.api.script;

import lombok.RequiredArgsConstructor;

/**
 *
 * 命令行作业的实现
 * @author fangyanpeng
 */
@RequiredArgsConstructor
public class ScriptJobImpl implements ScriptJob{

    private final String commandLine;

    @Override
    public String getCommandLine() {
        return null;
    }
}
