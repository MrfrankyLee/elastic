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

    private static String RABBITMQ_CONSUME_REPEAT_KEY = "rabbitmq:consume:repeat:key:";
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

    public String getConsumeStatus(String messageId) {
        return redisTemplate.opsForValue().get(RABBITMQ_CONSUME_REPEAT_KEY + messageId);
    }

    public void delete(String messageId) {
        redisTemplate.delete(RABBITMQ_CONSUME_REPEAT_KEY + messageId);
    }

    public void markConsumed(String messageId, long dedupRecordReserveMinutes) {
        redisTemplate.opsForValue().set(RABBITMQ_CONSUME_REPEAT_KEY + messageId, ConsumeStatusEnum.CONSUME_STATUS_CONSUMED.getValue(), dedupRecordReserveMinutes, TimeUnit.MINUTES);
    }
}
