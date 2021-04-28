package com.needayeah.elastic.config.es;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;

/**
 * @author lixiaole
 * @desc elasticSearch 资源加载配置
 * @date 2021/2/2
 */
@Configuration
@Conditional(value = EsConfiguration.EnableEsCondition.class)
public class EsConfiguration {
    /** 集群地址，多个用,隔开 */
    @Value("${elasticsearch.restHighLevel.Client.hosts:127.0.0.1:9200}")
    private String hosts;

    /** 使用的协议 */
    @Value("${elasticsearch.restHighLevel.Client.schema:http}")
    private String schema;

    /** 连接超时时间 */
    @Value("${elasticsearch.restHighLevel.Client.connectTimeOut:1000}")
    private int connectTimeOut;

    /** 连接超时时间 */
    @Value("${elasticsearch.restHighLevel.Client.socketTimeOut:30000}")
    private int socketTimeOut;

    /** 获取连接的超时时间 */
    @Value("${elasticsearch.restHighLevel.Client.connectionRequestTimeOut:500}")
    private int connectionRequestTimeOut;

    /** 最大连接数 */
    @Value("${elasticsearch.restHighLevel.Client.maxConnectNum:100}")
    private int maxConnectNum;

    /** 最大路由连接数 */
    @Value("${elasticsearch.restHighLevel.Client.maxConnectPerRoute:100}")
    private int maxConnectPerRoute;

    /** 用户名 */
    @Value("${elasticsearch.username:}")
    private String userName;

    /** 密码 */
    @Value("${elasticsearch.password:}")
    private String password;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        ArrayList<HttpHost> hostList = new ArrayList<>();
        String[] hostsStr = hosts.split(",");
        for (String host : hostsStr) {
            if (Strings.isEmpty(host)) {
                continue;
            }
            String[] hostAndPort = host.split(",");
            if (hostAndPort.length == 2 && !Strings.isEmpty(hostAndPort[1])) {
                hostList.add(new HttpHost(hostAndPort[0], Integer.parseInt(hostAndPort[1]), schema));
            } else if (hostAndPort.length == 1) {
                hostList.add(new HttpHost(hostAndPort[0], 80, schema));
            }
        }
        RestClientBuilder builder = RestClient.builder(hostList.toArray(new HttpHost[0]))
                // 异步httpclient连接延时配置
                .setRequestConfigCallback(requestConfigBuilder -> {
                    requestConfigBuilder.setConnectTimeout(connectTimeOut);
                    requestConfigBuilder.setSocketTimeout(socketTimeOut);
                    requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeOut);
                    return requestConfigBuilder;
                })
                // 异步httpclient连接数配置
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    httpClientBuilder.setMaxConnTotal(maxConnectNum);
                    httpClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
                    if (StringUtil.isNotEmpty(userName) && StringUtil.isNotEmpty(password)) {
                        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    } else {
                        return httpClientBuilder;
                    }
                });

        return new RestHighLevelClient(builder);
    }


    public static class EnableEsCondition implements Condition {
        /**
         * 动态启用elasticSearch
         */
        @Value("elasticsearch.restHighLevel.Client.enable:true")
        private Boolean enable;

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return enable;
        }
    }

}
