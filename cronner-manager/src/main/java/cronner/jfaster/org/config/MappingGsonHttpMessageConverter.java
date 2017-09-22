package cronner.jfaster.org.config;

import cronner.jfaster.org.exeception.JobSystemException;
import cronner.jfaster.org.util.json.GsonFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.nio.charset.Charset;
/**
 *
 * @author fangyanpeng
 */
public class MappingGsonHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    public static final String ENCODE = "UTF-8";

    public static  final Charset DEFAULT_CHARSET = Charset.forName(ENCODE);

    //分隔符
    public static final String DELIM = "&";

    public static final MediaType DEFAULT_TYPE = new MediaType("text", "plain", Charset.forName(ENCODE));


    public MappingGsonHttpMessageConverter() {
        super(DEFAULT_TYPE,new MediaType("text", "html", Charset.forName("UTF-8")), new MediaType("application", "json", DEFAULT_CHARSET));
    }

    @Override
    protected boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return true;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return true;
    }

    /**
     * 只支持post的 对象传输， 当时get是，流里面读出的数据为空
     * @RequestBody  走这里 ,
     * 参数用@ModelAttribute  既支持post  也支持get
     * 参数直接是pojo的属性
     *
     */
    @Override
    protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException,
            HttpMessageNotReadableException {
        String param = FileCopyUtils
                .copyToString(new InputStreamReader(inputMessage.getBody(), ENCODE));
        param = URLDecoder.decode(param, ENCODE);
        if (clazz.equals(String.class)) {
            return param;
        }

        if (param.startsWith("{") && param.endsWith("}")) { // json格式直接转换,这里判断一下，是怕json格式里面的数据也包含= 。所以先加一个判断
            return GsonFactory.getGson().fromJson(param, clazz);
        }

        if (param.contains("=")) { // 参数形式，手动转换 ，支持post参数提交. 参考httpClient.postBody 方法
            JSONObject jo = new JSONObject();
            String[] split = param.split(DELIM);
            for (String s : split) {
                String[] ss = s.split("=");
                if (ss.length == 2) {
                    try {
                        jo.put(ss[0], ss[1]);
                    } catch (JSONException e) {
                        throw new JobSystemException(e);
                    }
                }
            }
            if (jo.length() != 0)
                return GsonFactory.getGson().fromJson(jo.toString(), clazz);
        }
        return GsonFactory.getGson().fromJson(param, clazz);
    }

    @Override
    protected void writeInternal(Object t, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        // 非常重要，设置页面展示编码
        outputMessage.getHeaders().setContentType(DEFAULT_TYPE);
        if (t == null)
            return;
        if (t instanceof String) {
            FileCopyUtils.copy((String) t, new OutputStreamWriter(outputMessage.getBody(), ENCODE));
        } else {
            String s = GsonFactory.getGson().toJson(t);
            FileCopyUtils.copy(s, new OutputStreamWriter(outputMessage.getBody(), ENCODE));
        }

    }

}
