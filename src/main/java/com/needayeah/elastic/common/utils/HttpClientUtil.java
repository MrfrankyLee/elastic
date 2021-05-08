package com.needayeah.elastic.common.utils;

import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.NoRouteToHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by tahier on 16/5/6.
 * httpClient 基础类
 */
@SuppressWarnings("unused")
@Slf4j
public class HttpClientUtil {

    private final static int MAX_TOTAL_CONNECTIONS = 200;
    private final static int MAX_ROUTE_CONNECTIONS = 200;
    private final static int READ_TIMEOUT = 6000;
    private final static int XINDE_READ_TIMEOUT = 30000;
    private final static int TEST_READ_TIMEOUT = 3000;
    private final static int CONNECT_TIMEOUT = 4000;
    private final static int TEST_CONNECT_TIMEOUT = 2000;
    private final static int WAIT_TIMEOUT = 100;
    private static CloseableHttpClient client;

    private static CloseableHttpClient xinDeClient;

    private static RequestConfig testRequestConfig = RequestConfig.custom().setConnectionRequestTimeout(WAIT_TIMEOUT)
            .setConnectTimeout(TEST_CONNECT_TIMEOUT).setSocketTimeout(TEST_READ_TIMEOUT).build();

    static {
        SSLContext sslcontext = SSLContexts.createSystemDefault();
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslcontext))
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectionRequestTimeout(WAIT_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(READ_TIMEOUT).build();
        RequestConfig XinDeRequestConfig = RequestConfig.custom().setConnectionRequestTimeout(WAIT_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(XINDE_READ_TIMEOUT).build();
        connectionManager.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
        client = HttpClients.custom().setConnectionManager(connectionManager)
                .setDefaultRequestConfig(defaultRequestConfig).build();
        xinDeClient = HttpClients.custom().setConnectionManager(connectionManager)
                .setDefaultRequestConfig(XinDeRequestConfig).build();
    }


    public static String sendGetRequest(String url) throws Exception {
        return sendGetRequest(url, null);
    }

    public static String sendTestRequest(String url) throws Exception {
        return sendTestRequest(url, null);
    }

    /**
     * 发送http Get请求
     *
     * @param url    请求地址
     * @param params 请求的参数
     * @throws Exception
     */
    public static String sendGetRequestParam(String url, Map<String, String> params) throws Exception {
        StringBuilder builder = new StringBuilder(url);
        if (!url.contains("?")) {
            builder.append("?");
        }
        builder.append(Joiner.on("&").withKeyValueSeparator("=").join(params));

        return sendGetRequest(builder.toString(), null);
    }

    public static String sendMd5AndTsGetRequest(String url, TreeMap<String, String> params, String key) throws Exception {
        params.put("ts", String.valueOf(System.currentTimeMillis() / 1000));
        String encryptedStr = MD5Util.md5(params, key);
        params.put("sign", encryptedStr);
        return sendGetRequestParam(url, params);
    }

    public static String sendGetRequest(String url, String decodeCharset) throws Exception {
        long start = System.currentTimeMillis();
        String responseContent = null;

        HttpGet httpGet = new HttpGet(url);
        HttpEntity entity = null;
        try {
            HttpResponse response = client.execute(httpGet);
            entity = response.getEntity();
            if (null != entity) {
                responseContent = EntityUtils.toString(entity, decodeCharset == null ? "UTF-8" : decodeCharset);
            }

        } catch (Exception e) {
            log.error("访问" + url + "异常,信息如下", e);
            throw e;
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (Exception ex) {
                log.error("net io exception ", ex);
            }
        }
        return responseContent;
    }

    public static String sendPostRequest(String url, Map<String, String> params) throws Exception {
        return sendPostRequest(url, params, null);
    }

    public static String sendMd5AndTsPostRequest(String url, TreeMap<String, String> params, String key) throws Exception {
        params.put("ts", String.valueOf(System.currentTimeMillis() / 1000));
        String encryptedStr = MD5Util.md5(params, key);
        params.put("sign", encryptedStr);
        return sendPostRequest(url, params);
    }

    public static String sendPostRequest(String url, Map<String, String> params, String decodeCharset) throws Exception {
        long start = System.currentTimeMillis();
//        log.info("url =" + url);
        HttpPost post = new HttpPost(url);
        List<BasicNameValuePair> postData = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            postData.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        HttpEntity httpEntity = null;
        String responseContent = null;
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData, decodeCharset == null ? "UTF-8" : decodeCharset);
            post.setEntity(entity);
            HttpResponse response = client.execute(post);

            httpEntity = response.getEntity();
            if (httpEntity != null) {
                responseContent = EntityUtils.toString(httpEntity, decodeCharset == null ? "UTF-8" : decodeCharset);
            }
        } catch (Exception ex) {
            log.error("访问" + url + "异常,信息如下", ex);
            throw ex;
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (Exception ex) {
                log.error("net io exception ", ex);
            }
        }
        return responseContent;
    }

    public static String sendPostRequest(String url, String requestContent, String decodeCharset) {
        long start = System.currentTimeMillis();
        HttpPost post = new HttpPost(url);

        HttpEntity httpEntity = null;
        String responseContent = null;
        try {
         /*   if(client.getParams()!=null){
                client.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
            }*/
            post.setEntity(new StringEntity(requestContent, "UTF-8"));
            HttpResponse response = client.execute(post);
            httpEntity = response.getEntity();
            if (httpEntity != null) {
                responseContent = EntityUtils.toString(httpEntity, decodeCharset == null ? "UTF-8" : decodeCharset);
            }
        } catch (Exception ex) {
            log.error("访问" + url + "异常,信息如下", ex);
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (Exception ex) {
                log.error("net io exception ", ex);
            }
        }
        return responseContent;
    }

    public static String sendPostJsonRequest(String url, String json, String decodeCharset) {
        long start = System.currentTimeMillis();
        int i = 0;
        HttpEntity httpEntity = null;
        String responseContent = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            HttpResponse response = client.execute(httpPost);
            httpEntity = response.getEntity();
            if (httpEntity != null)
                responseContent = EntityUtils.toString(httpEntity, decodeCharset == null ? "UTF-8" : decodeCharset);
        } catch (NoHttpResponseException e) {
            i++;
            sendPostJsonRequest(url, json, decodeCharset);
            if (i >= 3) {
                log.error("未响应", e);
                return null;
            }

            return null;
        } catch (ConnectTimeoutException e) {
            sendPostJsonRequest(url, json, decodeCharset);
            i++;
            if (i >= 3) {
                log.error("连接超时", e);
                return null;
            }
        } catch (NoRouteToHostException e) {
            log.error("网络连接问题", e);
            return null;
        } catch (Exception e) {
            log.error("调第三方接口发生异常", e);
            return null;

        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (Exception ex) {
                log.error("net io exception ", ex);
            }
        }
        return responseContent;
    }

    public static String sendPostJsonRequest(String url, String json, Map<String, String> headers) {
        return sendPostJsonRequest(url, json, headers, null);
    }

    public static String sendPostJsonRequest(String url, String json, Map<String, String> headers, String decodeCharset) {
        long start = System.currentTimeMillis();
        int i = 0;
        HttpEntity httpEntity = null;
        String responseContent = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-type", "application/json;charset=utf-8");
            headers.forEach(httpPost::setHeader);
            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            HttpResponse response = client.execute(httpPost);
            httpEntity = response.getEntity();
            if (httpEntity != null) {
                responseContent = EntityUtils.toString(httpEntity, decodeCharset == null ? "UTF-8" : decodeCharset);
            }
        } catch (NoHttpResponseException e) {
            i++;
            sendPostJsonRequest(url, json,headers, decodeCharset);
            if (i >= 3) {
                log.error("未响应", e);
                return null;
            }

            return null;
        } catch (ConnectTimeoutException e) {
            sendPostJsonRequest(url, json,headers, decodeCharset);
            i++;
            if (i >= 3) {
                log.error("连接超时", e);
                return null;
            }
        } catch (NoRouteToHostException e) {
            log.error("网络连接问题", e);
            return null;
        } catch (Exception e) {
            log.error("调第三方接口发生异常", e);
            return null;

        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (Exception ex) {
                log.error("net io exception ", ex);
            }
        }
        return responseContent;
    }

    /**
     * 提交表单数据
     *
     * @param url
     * @param map
     * @param decodeCharset
     * @return
     */
    public static String sendPostFormRequest(String url, Map<String, String> map, String decodeCharset) {
        long start = System.currentTimeMillis();
        int i = 0;
        HttpEntity httpEntity = null;
        String responseContent = "";
        StringBuffer param = new StringBuffer("");
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);

            //装填参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            if (map != null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                    param.append(entry.getKey() + ":" + entry.getValue() + ",");
                }
            }
            //设置参数到请求对象中
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, decodeCharset == null ? "UTF-8" : decodeCharset));

            //设置header信息
            //指定报文头【Content-type】、【User-Agent】
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            // 执行http请求
            HttpResponse response = client.execute(httpPost);
            httpEntity = response.getEntity();
            if (httpEntity != null) {
                responseContent = EntityUtils.toString(httpEntity, decodeCharset == null ? "UTF-8" : decodeCharset);
            }
        } catch (NoHttpResponseException e) {
            i++;
            sendPostFormRequest(url, map, decodeCharset);
            if (i >= 3) {
                log.error("未响应", e);
                return null;
            }

            return null;
        } catch (ConnectTimeoutException e) {
            sendPostFormRequest(url, map, decodeCharset);
            i++;
            if (i >= 3) {
                log.error("连接超时", e);
                return null;
            }
        } catch (NoRouteToHostException e) {
            log.error("网络连接问题", e);
            return null;
        } catch (Exception e) {
            log.error("调第三方接口发生异常", e);
            return null;

        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (Exception ex) {
                log.error("net io exception ", ex);
            }
        }
        return responseContent;
    }

    public static String sendTestRequest(String url, String decodeCharset) throws Exception {
        long start = System.currentTimeMillis();
        String responseContent = null;

        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(testRequestConfig);
        HttpEntity entity = null;
        try {
            HttpResponse response = client.execute(httpGet);
            entity = response.getEntity();
            if (null != entity) {
                responseContent = EntityUtils.toString(entity, decodeCharset == null ? "UTF-8" : decodeCharset);
            }
        } catch (Exception e) {
            log.error("访问" + url + "异常,信息如下", e);
            throw e;
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (Exception ex) {
                log.error("net io exception ", ex);
            }
        }

        return responseContent;
    }


    public static String sendGetRequestNotLog(String url, String decodeCharset) throws Exception {
        long start = System.currentTimeMillis();
        String responseContent = null;

        HttpGet httpGet = new HttpGet(url);
        HttpEntity entity = null;
        try {
            HttpResponse response = client.execute(httpGet);
            entity = response.getEntity();
            if (null != entity) {
                responseContent = EntityUtils.toString(entity, decodeCharset == null ? "UTF-8" : decodeCharset);
            }
        } catch (Exception e) {
            log.error("访问" + url + "异常,信息如下", e);
            throw e;
        } finally {
            try {
                EntityUtils.consume(entity);
            } catch (Exception ex) {
                log.error("net io exception ", ex);
            }
        }
        return responseContent;
    }

    public static String sendPostRequestNotLog(String url, Map<String, String> params, String decodeCharset) throws Exception {
        long start = System.currentTimeMillis();
//        log.info("url =" + url);
        HttpPost post = new HttpPost(url);
        List<BasicNameValuePair> postData = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            postData.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        HttpEntity httpEntity = null;
        String responseContent = null;
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postData, decodeCharset == null ? "UTF-8" : decodeCharset);
            post.setEntity(entity);
            HttpResponse response = xinDeClient.execute(post);
//            int status = response.getStatusLine().getStatusCode();
//            if (status != 200) {
//
//            }
            httpEntity = response.getEntity();
            if (httpEntity != null) {
                responseContent = EntityUtils.toString(httpEntity, decodeCharset == null ? "UTF-8" : decodeCharset);
            }
            //RequestLog.POST.log(url, postData.toString(), responseContent, String.valueOf(System.currentTimeMillis() - start));
        } catch (Exception ex) {
            log.error("访问" + url + "异常,信息如下", ex);
            throw ex;
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (Exception ex) {
                log.error("net io exception ", ex);
            }
        }
        return responseContent;
    }

    public static String sendGetRequestForMoXie(String url, String token) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        // httpGet.setHeader("Accept-Encoding", "gzip,deflate,sdch");
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Authorization", "token " + token);

        HttpEntity entity;
        try {
            HttpResponse response = client.execute(httpGet);
            entity = response.getEntity();
            if (null != entity) {
                return EntityUtils.toString(entity, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            log.error("访问" + url + "异常,信息如下", e);
            throw e;
        }
        return null;
    }

    public static Map<String, String> sendPostRequestForHelibao(String url, Map<String, String> map, String decodeCharset) {
        long start = System.currentTimeMillis();
        int i = 0;
        HttpEntity httpEntity = null;
        String responseContent = "";
        String statusCode = "";

        StringBuffer param = new StringBuffer("");
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);

            //装填参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            if (map != null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                    param.append(entry.getKey() + ":" + entry.getValue() + ",");
                }
            }
            //设置参数到请求对象中
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, decodeCharset == null ? "UTF-8" : decodeCharset));

            //设置header信息
            //指定报文头【Content-type】、【User-Agent】
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            // 执行http请求
            HttpResponse response = client.execute(httpPost);
            statusCode = String.valueOf(response.getStatusLine().getStatusCode());
            httpEntity = response.getEntity();
            if (httpEntity != null) {
                responseContent = EntityUtils.toString(httpEntity, decodeCharset == null ? "UTF-8" : decodeCharset);
            }
        } catch (NoHttpResponseException e) {
            i++;
            sendPostRequestForHelibao(url, map, decodeCharset);
            if (i >= 3) {
                log.error("未响应", e);
                return null;
            }

            return null;
        } catch (ConnectTimeoutException e) {
            sendPostRequestForHelibao(url, map, decodeCharset);
            i++;
            if (i >= 3) {
                log.error("连接超时", e);
                return null;
            }
        } catch (NoRouteToHostException e) {
            log.error("网络连接问题", e);
            return null;
        } catch (Exception e) {
            log.error("调第三方接口发生异常", e);
            return null;

        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (Exception ex) {
                log.error("net io exception ", ex);
            }
        }
        Map<String, String> result = new HashMap<>();
        result.put("response", responseContent);
        result.put("statusCode", statusCode);
        return result;
    }

    public static Map<String, String> sendPostFormRequestForHelibao(String url, byte[] fileBytes, String filename, Map<String, String> params) {
        int i = 0;
        HttpEntity httpEntity = null;
        String statusCode = "";
        String responseContent = "";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        try {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", fileBytes, ContentType.create("multipart/form-data"), filename);
            params.forEach((k, v) -> {
                StringBody stringBody = new StringBody(v, ContentType.MULTIPART_FORM_DATA);
                builder.addPart(k, stringBody);
            });
            HttpEntity entity = builder.build();
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            entity = response.getEntity();

            statusCode = String.valueOf(response.getStatusLine().getStatusCode());
            httpEntity = response.getEntity();
            if (httpEntity != null) {
                responseContent = EntityUtils.toString(httpEntity, StandardCharsets.UTF_8);
            }

        } catch (NoHttpResponseException e) {
            i++;
            sendPostFormRequestForHelibao(url, fileBytes, filename, params);
            if (i >= 3) {
                log.error("未响应", e);
                return null;
            }

            return null;
        } catch (ConnectTimeoutException e) {
            sendPostFormRequestForHelibao(url, fileBytes, filename, params);
            i++;
            if (i >= 3) {
                log.error("连接超时", e);
                return null;
            }
        } catch (NoRouteToHostException e) {
            log.error("网络连接问题", e);
            return null;
        } catch (Exception e) {
            log.error("调第三方接口发生异常", e);
            return null;

        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (Exception ex) {
                log.error("net io exception ", ex);
            }
        }
        Map<String, String> result = new HashMap<>();
        result.put("response", responseContent);
        result.put("statusCode", statusCode);
        return result;
    }


    public String postSFAPI(String url, String xml, String verifyCode) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        List<NameValuePair> parameters = new ArrayList();
        parameters.add(new BasicNameValuePair("xml", xml));
        parameters.add(new BasicNameValuePair("verifyCode", verifyCode));
        HttpPost post = this.postForm(url, new UrlEncodedFormEntity(parameters, Charset.forName("UTF-8")));
        String body = "";
        body = this.invoke(httpClient, post);

        try {
            httpClient.close();
        } catch (IOException var9) {
            log.error("HttpClientService post error", var9);
        }

        return body;
    }

    public String get(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        String body = "";
        body = this.invoke(httpClient, get);

        try {
            httpClient.close();
        } catch (IOException var6) {
            log.error("HttpClientService get error", var6);
        }

        return body;
    }



    public String invoke(CloseableHttpClient httpclient, HttpUriRequest httpost) {
        HttpResponse response = sendRequest(httpclient, httpost);
        String body = "";
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            body = parseResponse(response);
        }

        return body;
    }

    private static String parseResponse(HttpResponse response) {
        HttpEntity entity = response.getEntity();
        String body = "";

        try {
            if (entity != null) {
                body = EntityUtils.toString(entity);
            }
        } catch (ParseException var4) {
            log.error("HttpClientService paseResponse error", var4);
        } catch (IOException var5) {
            log.error("HttpClientService paseResponse error", var5);
        }

        return body;
    }

    private static HttpResponse sendRequest(CloseableHttpClient httpclient, HttpUriRequest httpost) {
        CloseableHttpResponse response = null;

        try {
            response = httpclient.execute(httpost);
        } catch (ClientProtocolException var4) {
            log.error("HttpClientService sendRequest error", var4);
        } catch (IOException var5) {
            log.error("HttpClientService sendRequest error", var5);
        }

        return response;
    }

    public HttpPost postForm(String url, StringEntity entity) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(entity);
        return httpPost;
    }
}
