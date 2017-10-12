package cronner.jfaster.org.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 *
 * @author fangyanpeng
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BlockUtils {
    
    public static void waitingShortTime() {
        sleep(100L);
    }

    public static void waitingShortTime(long time) {
        sleep(time);
    }
    
    public static void sleep(final long millis) {
        try {
            Thread.sleep(millis);
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
