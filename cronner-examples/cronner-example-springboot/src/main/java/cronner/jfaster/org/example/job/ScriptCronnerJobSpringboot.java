package cronner.jfaster.org.example.job;

import cronner.jfaster.org.job.annotation.Job;
import cronner.jfaster.org.example.job.listener.JobListenerExample;
import cronner.jfaster.org.job.api.script.ScriptJob;

import java.util.concurrent.TimeUnit;

/**
 * @author fangyanpeng
 */
@Job(name = "cronner-script-job",listener = JobListenerExample.class)
public class ScriptCronnerJobSpringboot implements ScriptJob {

    @Override
    public String getCommandLine() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "sh /Users/fangyanpeng/gitproject/cronner/cronner-examples/cronner-example-springboot/src/main/resources/script/demo.sh";
    }
}
