package cronner.jfaster.org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author fangyanpeng
 */
@SpringBootApplication
public class CronnerSpringBootMain {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CronnerSpringBootMain.class,args);
    }
}
