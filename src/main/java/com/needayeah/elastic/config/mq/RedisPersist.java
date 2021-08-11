package com.needayeah.elastic.config.mq;

import com.needayeah.elastic.config.mq.enums.ConsumeStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author lixiaole
 * @date 2021/8/4
 */
@Component
public class RedisPersist {

    /**
     * 消息重复消费去重key
     */
    private static String RABBITMQ_CONSUME_REPEAT_KEY = "rabbitmq:consume:repeat:key:";

    /**
     * 消息消费次数key
     */
    private static String RABBITMQ_CONSUME_COUNT_KEY = "rabbitmq:consume:count:key:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 检查是否消费过该条消息
     *
     * @param messageId 消息id
     * @return
     */
    public boolean setConsumingIfNx(String messageId, long expireTime) {
        return redisTemplate.opsForValue().setIfAbsent(RABBITMQ_CONSUME_REPEAT_KEY + messageId, ConsumeStatusEnum.CONSUME_STATUS_CONSUMING.getValue(), expireTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取消息消费状态
     *
     * @param messageId 消息id
     * @return
     */
    public String getConsumeStatus(String messageId) {
        return redisTemplate.opsForValue().get(RABBITMQ_CONSUME_REPEAT_KEY + messageId);
    }

    /**
     * 删除消息去重key
     *
     * @param messageId 消息id
     * @return
     */
    public void delete(String messageId) {
        redisTemplate.delete(RABBITMQ_CONSUME_REPEAT_KEY + messageId);
    }

    /**
     * 统计消息的重试消费次数(自增)
     *
     * @param messageId 消息id
     * @return
     */
    public long increment(String messageId) {
        long count = redisTemplate.opsForValue().increment(RABBITMQ_CONSUME_COUNT_KEY + messageId);
        redisTemplate.expire(RABBITMQ_CONSUME_COUNT_KEY + messageId, 60, TimeUnit.MINUTES);
        return count;
    }

    /**
     * 设置消息消费状态为已完成
     *
     * @param messageId 消息id
     */
    public void markConsumed(String messageId, long dedupRecordReserveMinutes) {
        redisTemplate.opsForValue().set(RABBITMQ_CONSUME_REPEAT_KEY + messageId, ConsumeStatusEnum.CONSUME_STATUS_CONSUMED.getValue(), dedupRecordReserveMinutes, TimeUnit.MINUTES);
    }
}
