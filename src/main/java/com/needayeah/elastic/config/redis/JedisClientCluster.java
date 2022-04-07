package com.needayeah.elastic.config.redis;

import org.apache.http.util.Asserts;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisClusterInfoCache;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.util.JedisClusterCRC16;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lixiaole
 * @date 2021/4/30
 */
@Component
public class JedisClientCluster implements JedisClient {

    @Autowired(required = false)
    private JedisCluster jedisCluster;

    private final int MAX_OPERATE_NUM = 1000;

    @Override
    public String set(String key, String value) {
        return jedisCluster.set(key, value);
    }

    @Override
    public String get(String key) {
        return jedisCluster.get(key);
    }

    @Override
    public Boolean exists(String key) {
        return jedisCluster.exists(key);
    }

    @Override
    public Long expire(String key, int seconds) {
        return jedisCluster.expire(key, seconds);
    }

    @Override
    public Long ttl(String key) {
        return jedisCluster.ttl(key);
    }

    @Override
    public Long incr(String key) {
        return jedisCluster.incr(key);
    }

    @Override
    public Long hset(String key, String field, String value) {
        return jedisCluster.hset(key, field, value);
    }

    @Override
    public String hget(String key, String field) {
        return jedisCluster.hget(key, field);
    }

    @Override
    public Long hdel(String key, String... field) {
        return jedisCluster.hdel(key, field);
    }

    @Override
    public Long lpush(String key, String... field) {
        return jedisCluster.lpush(key, field);
    }

    @Override
    public String rpop(String key) {
        return jedisCluster.rpop(key);
    }

    @Override
    public Long delete(String key) {
        return jedisCluster.del(key);
    }

    @Override
    public Long hincrBy(String key, String field, long value) {
        return jedisCluster.hincrBy(key, field, value);
    }

    @Override
    public Boolean setBit(String key, long offset, boolean value) {
       return jedisCluster.setbit(key,offset,value);
    }

    @Override
    public Boolean getBit(String key, long offset){
        return jedisCluster.getbit(key,offset);
    }

    /**
     * 批量get
     *
     * @param keys
     * @return 每个key对应的获取结果List。无论key在redis中是否能获取到对应的值，都会在List体现出来，如果某一个key获取到的值是null，那么他在结果list中就是null
     * @throws Exception
     */
    @Override
    public List<String> mGet(List<String> keys){
        List<String> resList = new ArrayList<>();
        if (keys == null || keys.size() == 0) {
            return resList;
        }
        Asserts.check(keys.size() <= MAX_OPERATE_NUM, "操作key的数量不能超过" + MAX_OPERATE_NUM);

        if (keys.size() == 1) {
            resList.add(jedisCluster.get(keys.get(0)));
            return resList;
        }

        Map<JedisPool, List<String>> jedisPoolMap = getJedisPool(keys);
        List<String> keyList;
        JedisPool currentJedisPool;
        Pipeline currentPipeline = null;
        List<Object> res = new ArrayList<>();
        Map<String, String> resultMap = new HashMap<>();
        for (Map.Entry<JedisPool, List<String>> entry : jedisPoolMap.entrySet()) {
            Jedis jedis = null;
            try {
                currentJedisPool = entry.getKey();
                keyList = entry.getValue();
                try {
                    //获取pipeline
                    jedis = currentJedisPool.getResource();
                    currentPipeline = jedis.pipelined();
                    for (String key : keyList) {
                        currentPipeline.get(key);
                    }
                    //从pipeline中获取结果
                    res = currentPipeline.syncAndReturnAll();
                } catch (Exception e) {
                    throw new Exception();
                }finally {
                    if(currentPipeline != null){
                        currentPipeline.close();
                    }
                }

                for (int i = 0; i < keyList.size(); i++) {
                    resultMap.put(keyList.get(i), res.get(i) == null ? null : res.get(i).toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                returnResource(jedis);
            }
        }

        List<String> resultList = new ArrayList<>();
        for (String key : keys) {
            resultList.add(resultMap.get(key));
        }
        return resultList;
    }


    /**
     * 批量删除多个Keys
     *
     * @param keys
     * @return 每个key对应的执行结果List。true：表示删除了；false：表示没有删除
     * @throws Exception
     */
    @Override
    public List<Boolean> mDel(List<String> keys){
        List<Boolean> resList = new ArrayList<>();
        if (keys == null || keys.size() == 0) {
            return resList;
        }
        Asserts.check(keys.size() <= MAX_OPERATE_NUM, "操作key的数量不能超过" + MAX_OPERATE_NUM);

        if (keys.size() == 1) {
            Long result = jedisCluster.del(keys.get(0));
            resList.add(result == null ? null : Long.parseLong(result.toString()) > 0);
            return resList;
        }

        Map<JedisPool, List<String>> jedisPoolMap = getJedisPool(keys);
        List<String> keyList;
        JedisPool currentJedisPool;
        Pipeline currentPipeline = null;
        List<Object> res = new ArrayList<>();
        Map<String, Boolean> resultMap = new HashMap<>();
        for (Map.Entry<JedisPool, List<String>> entry : jedisPoolMap.entrySet()) {
            Jedis jedis = null;
            try {
                currentJedisPool = entry.getKey();
                keyList = entry.getValue();
                try {
                    //获取pipeline
                    jedis = currentJedisPool.getResource();
                    currentPipeline = jedis.pipelined();
                    for (String key : keyList) {
                        currentPipeline.del(key);
                    }
                    //从pipeline中获取结果
                    res = currentPipeline.syncAndReturnAll();
                } catch (Exception e) {
                    throw new Exception();
                } finally {
                    if(currentPipeline != null){
                        currentPipeline.close();
                    }
                }

                for (int i = 0; i < keyList.size(); i++) {
                    resultMap.put(keyList.get(i), res.get(i) == null ? null : Long.parseLong(res.get(i).toString()) > 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                returnResource(jedis);
            }
        }

        List<Boolean> resultList = new ArrayList<>();
        for (String key : keys) {
            resultList.add(resultMap.get(key));
        }
        return resultList;
    }


    private Map<JedisPool, List<String>> getJedisPool(List<String> keys){
        //JedisCluster继承了BinaryJedisCluster
        //BinaryJedisCluster的JedisClusterConnectionHandler属性
        //里面有JedisClusterInfoCache，根据这一条继承链，可以获取到JedisClusterInfoCache
        //从而获取slot和JedisPool直接的映射
        MetaObject metaObject = SystemMetaObject.forObject(jedisCluster);
        JedisClusterInfoCache cache = (JedisClusterInfoCache) metaObject.getValue("connectionHandler.cache");
        //保存地址+端口和命令的映射
        Map<JedisPool, List<String>> jedisPoolMap = new HashMap<>();

        JedisPool currentJedisPool;
        List<String> keyList;
        for (String key : keys) {
            //计算哈希槽
            int crc = JedisClusterCRC16.getSlot(key);
            //通过哈希槽获取节点的连接
            currentJedisPool = cache.getSlotPool(crc);

            //由于JedisPool作为value保存在JedisClusterInfoCache中的一个map对象中，每个节点的
            //JedisPool在map的初始化阶段就是确定的和唯一的，所以获取到的每个节点的JedisPool都是一样
            //的，可以作为map的key
            if (jedisPoolMap.containsKey(currentJedisPool)) {
                jedisPoolMap.get(currentJedisPool).add(key);
            } else {
                keyList = new ArrayList<>();
                keyList.add(key);
                jedisPoolMap.put(currentJedisPool, keyList);
            }
        }
        return jedisPoolMap;
    }

    /**
     * 释放jedis资源
     *
     * @param jedis
     */
    public void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
