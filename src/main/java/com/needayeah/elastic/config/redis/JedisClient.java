package com.needayeah.elastic.config.redis;

import java.util.List;

/**
 * @author lixiaole
 * @date 2021/4/30
 */
public interface JedisClient {

    String set(String key, String value);

    String get(String key);

    Boolean exists(String key);

    Long expire(String key, int seconds);

    Long ttl(String key);

    Long incr(String key);

    Long hset(String key, String field, String value);

    String hget(String key, String field);

    Long hdel(String key, String... field);

    Long lpush(String key, String... field);

    String rpop(String key);

    Long delete(String key);

    Long hincrBy(String key, String field, long value);

    Boolean setBit(String key, long offset, boolean value);

    Boolean getBit(String key, long offset);

    List<String> mGet(List<String> keys);

    List<Boolean> mDel(List<String> keys);
}
