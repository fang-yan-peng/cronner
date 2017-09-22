package cronner.jfaster.org;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import cronner.jfaster.org.api.ExecuteApi;
import cronner.jfaster.org.exeception.JobSystemException;
import cronner.jfaster.org.restful.RestfulServer;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * 启动restful服务
 * @author fangyanpeng
 */
@Slf4j
public class ExecutorRestfulBootstrap {

    public static final int DEFALUT_PORT = 9233;

    private static volatile RestfulServer server;

    private static AtomicBoolean stat = new AtomicBoolean(false);

    public static final String CRONNER_EXETUTOR_PORT_PARAM = "cronner.executor.port";

    public static int start(){
        int port = DEFALUT_PORT;
        //如果是多个程序占用不同的端口，通过java -Dcronner.executor.port=xx 指定
        String portStr = System.getProperty(CRONNER_EXETUTOR_PORT_PARAM);
        if(!Strings.isNullOrEmpty(portStr)){
            port = Integer.parseInt(portStr);
        }
        if(stat.compareAndSet(false,true)) {
            server = new RestfulServer(port);
            try {
                server.start(ExecuteApi.class.getPackage().getName(), Optional.<String>absent());
            } catch (Exception e) {
                log.error("Start restful server fail",e);
                throw new JobSystemException(e);
            }

        }
        return port;
    }

    public static void stop(){
        if(stat.compareAndSet(true,false)){
            server.stop();
        }
    }
}
