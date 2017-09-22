package cronner.jfaster.org;

import cronner.jfaster.org.service.CreateTalbleService;
import cronner.jfaster.org.service.LoadJobService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author fangyanpeng
 */
@SpringBootApplication
public class CronnerManager {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CronnerManager.class, args);

        //建立数据库表
        CreateTalbleService talbleService = context.getBean(CreateTalbleService.class);
        talbleService.createTables();

        //节点启动加载作业
        LoadJobService loadJob = context.getBean(LoadJobService.class);
        loadJob.load();
    }

}
