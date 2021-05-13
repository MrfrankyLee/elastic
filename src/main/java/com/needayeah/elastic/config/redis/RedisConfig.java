package com.needayeah.elastic.config.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * @author lixiaole
 * @date 2021/3/3
 */
@Configuration
@Conditional(RedisConfig.EnableRedisCondition.class)
public class RedisConfig {

    @Value("${redis.nodes:}")
    private String nodes;

    @Value("${redis.password:}")
    private String passWord;

    @Value("${redis.connectionTimeout:5000}")
    private int connectionTimeout;

    @Value("${redis.soTimeout:3000}")
    private int soTimeout;

    @Value("${redis.maxAttempts:5}")
    private int maxAttempts;


    @Value("${redis.maxTotal:500}")
    private int maxTotal;

    @Value("${redis.maxIdle:30}")
    private int maxIdle;

    @Value("${redis.minIdle:5}")
    private int minIdle;


    @Bean
    public JedisCluster getJedisCluster() {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        //获取redis集群的ip及端口号等相关信息；
        String[] serverArray = nodes.split(",");
        Set<HostAndPort> nodes = new HashSet<>();

        //遍历add到HostAndPort中；
        for (String ipPort : serverArray) {
            String[] ipPortPair = ipPort.split(":");
            nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim())));
        }
        //构建对象并返回；
        JedisCluster jedisCluster = new JedisCluster(nodes, connectionTimeout, soTimeout, maxAttempts, passWord, poolConfig);
        return jedisCluster;
    }


    public static class EnableRedisCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            // 可加入配置中心 动态开闭redis
            //String enable = ConfigService.getAppConfig().getProperty("redis.nodes", "");
            String enable = Strings.EMPTY;
            return Strings.isEmpty(enable);
        }
    }
}
