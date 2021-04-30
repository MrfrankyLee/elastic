package com.needayeah.elastic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

/**
 * @author lixiaole
 * @date 2021/3/3
 */
@Configuration
public class JedisConfig {

    @Bean
    public static Jedis jedis(){
        Jedis jedis = new Jedis("127.0.0.1");
        jedis.auth("");
        return jedis;
    }


}
