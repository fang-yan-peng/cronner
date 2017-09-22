package cronner.jfaster.org.util.http;

import com.google.common.base.Strings;
import cronner.jfaster.org.exeception.JobSystemException;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 处理http请求
 *
 * @author fangyanpeng
 */
public class Http {
    private String url;
    private HttpMethod method;
    private Map<String, String> headers;
    private Map<String, String> params;
    private String body;
    private Integer connectTimeout;
    private String contentType;
    private String charset;

    public static final String JSON_CONNTENT_TYPE = "application/json;charset=utf-8";

    public static final String TEXT_CONNTENT_TYPE = "text/json";

    public Http(String url) {
        this.method = HttpMethod.GET;
        this.headers = Collections.emptyMap();
        this.params = Collections.emptyMap();
        this.connectTimeout = Integer.valueOf(5000);
        this.contentType = "";
        this.charset = "UTF-8";
        this.url = url;
    }

    public Http method(HttpMethod method) {
        this.method = method;
        return this;
    }

    public Http headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public Http params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    public Http body(String body) {
        this.body = body;
        return this;
    }


    public Http contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public Http charset(String charset) {
        this.charset = charset;
        return this;
    }

    public Http connTimeout(Integer connectTimeout) {
        this.connectTimeout = Integer.valueOf(connectTimeout.intValue() * 1000);
        return this;
    }

    public String request() throws IOException {
        switch(method.ordinal()) {
            case 0:
                return this.sendGET();
            case 1:
                return this.sendPOST();
            case 2:
                return this.sendPUT();
            default:
                return null;
        }
    }

    public String request(int retry) throws Exception {
        int i = 1;
        while (i <= retry){
            try {
                return request();
            } catch (Throwable e) {
                if(i == retry){
                    throw e;
                }
                ++i;
                TimeUnit.SECONDS.sleep(2);
            }
        }

        throw new JobSystemException("Can not run here");
    }

    private String sendGET() throws IOException {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(connectTimeout).build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(config).build();
        HttpGet httpGet = new HttpGet(url);
        configRequest(httpGet);
        return doRequest(httpGet,httpClient);

    }

    private String sendPOST() throws IOException {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(connectTimeout).build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(config).build();
        HttpPost httpPost = new HttpPost(url);
        configRequest(httpPost);
        List<NameValuePair> paramNV = null;
        if (params != null && params.size() > 0) {
            paramNV = new ArrayList<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                paramNV.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        if (paramNV != null) {
            httpPost.setEntity(new UrlEncodedFormEntity(paramNV, charset));
        }
        return doRequest(httpPost,httpClient);
    }

    private String sendPUT() throws IOException {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(connectTimeout).build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(config).build();
        HttpPut httpPut = new HttpPut(url);
        configRequest(httpPut);
        if(!Strings.isNullOrEmpty(body)){
            StringEntity se = new StringEntity(body);
            se.setContentType(TEXT_CONNTENT_TYPE);
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, contentType));
            httpPut.setEntity(se);
        }
        return doRequest(httpPut,httpClient);
    }

    private void configRequest(HttpRequestBase requestBase){
        if(headers != null){
            for (Map.Entry<String,String> header : headers.entrySet()){
                requestBase.addHeader(header.getKey(),header.getValue());
            }
        }
        requestBase.addHeader(HTTP.CONTENT_TYPE, contentType);
    }

    private String doRequest(HttpRequestBase requestBase, CloseableHttpClient httpClient) throws IOException {
        CloseableHttpResponse response = null;
        HttpEntity entity = null;
        try {
            response = httpClient.execute(requestBase);
            entity = response.getEntity();
            return EntityUtils.toString(entity, charset);
        } finally {
            if(entity != null){
                EntityUtils.consume(entity);
            }
            if(response != null){
                response.close();
            }
        }
    }

}
