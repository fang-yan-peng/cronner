package cronner.jfaster.org.util.date;

import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author fangyanpeng
 */
public class DateUtil {

    public static Date addSecond(Date date, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, second);
        return calendar.getTime();
    }
}
