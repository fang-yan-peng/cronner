package cronner.jfaster.org.example;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.CountDownLatch;

/**
 * @author fangyanpeng
 */
public class CronnerSpringMain {
    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:job.xml");
        ctx.start();
        CountDownLatch latch = new CountDownLatch(1);
        latch.await();
    }
}
