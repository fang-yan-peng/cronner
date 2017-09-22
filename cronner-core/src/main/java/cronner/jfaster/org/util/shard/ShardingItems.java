package cronner.jfaster.org.util.shard;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 分片项工具类.
 * @author fangyanpeng
 */
@Getter
public final class ShardingItems {
    
    private static final String DELIMITER = ",";
    
    /**
     * 根据分片项字符串获取分片项列表.
     *
     * @param itemsString 分片项字符串
     * @return 分片项列表
     */
    public static List<Integer> toItemList(final String itemsString) {
        if (Strings.isNullOrEmpty(itemsString)) {
            return Collections.emptyList();
        }
        String[] items = itemsString.split(DELIMITER);
        List<Integer> result = new ArrayList<>(items.length);
        for (String each : items) {
            int item = Integer.parseInt(each);
            if (!result.contains(item)) {
                result.add(item);
            }
        }
        return result;
    }
    
    /**
     * 根据分片项列表获取分片项字符串.
     *
     * @param items 分片项列表
     * @return 分片项字符串
     */
    public static String toItemsString(final List<Integer> items) {
        return items.isEmpty() ? "" : Joiner.on(DELIMITER).join(items);
    }
}
