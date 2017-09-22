package cronner.jfaster.org.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Gson构建器.
 *
 * @author fangyanpeng
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GsonFactory {
    
    private static final GsonBuilder GSON_BUILDER = new GsonBuilder();
    
    private static volatile Gson gson = GSON_BUILDER.create();


    static {
        //注册Date类解析，默认的解析如果Date类型的字段为null，会npt
        registerTypeAdapter(Date.class,new DateTypeAdapter(Date.class,"yyyy-MM-dd HH:mm:ss"));
    }
    
    /**
     * 注册Gson解析对象.
     * 
     * @param type Gson解析对象类型
     * @param typeAdapter Gson解析对象适配器
     */
    public synchronized static void registerTypeAdapter(final Type type, final TypeAdapter typeAdapter) {
        GSON_BUILDER.registerTypeAdapter(type, typeAdapter);
        gson = GSON_BUILDER.create();
    }
    
    /**
     * 获取Gson实例.
     * 
     * @return Gson实例
     */
    public static Gson getGson() {
        return gson;
    }
}
