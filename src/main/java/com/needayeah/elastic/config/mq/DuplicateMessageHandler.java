package com.needayeah.elastic.config.mq;

import com.needayeah.elastic.config.mq.enums.ConsumeStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author lixiaole
 * @date 2021/8/4
 * @desc 重复消息处理助手基于Redis
 */
@Component
@Slf4j
public class DuplicateMessageHandler {

    /**
     * 对于消费中的消息，多少毫秒内认为重复，默认一分钟，即一分钟内的重复消息都会串行处理（等待前一个消息消费成功/失败），超过这个时间如果消息还在消费就不认为重复了（为了防止消息丢失）
     */
    private long dedupProcessingExpireMilliSeconds = 60 * 1000;

    /**
     * 消息消费成功后，记录保留多少分钟，默认一天，即一天内的消息不会重复
     */
    private long dedupRecordReserveMinutes = 60 * 24;

    /**
     * 当前消息的最大重试消费次数
     */
    private static final long MAX_CONSUME_COUNT = 3;

    @Autowired
    private RedisPersist redisPersist;

    public boolean handleMsgInner(Message message, IConsumeService consumeService) {
        String messageId = message.getMessageProperties().getMessageId();
        if (redisPersist.increment(messageId) > MAX_CONSUME_COUNT) {
            // 已经消费失败超过最大消费次数了(后面再重试大概率也是失败)那就默认其成功了吧  后面进行人工干预
            // TODO  进行人工干预
            log.warn("consume this message " + MAX_CONSUME_COUNT + " times , but all failed!");
            return true;
        }
        Boolean shouldConsume = redisPersist.setConsumingIfNx(messageId, dedupProcessingExpireMilliSeconds);
        // 没有消费过
        if (Objects.nonNull(shouldConsume) && shouldConsume) {
            // 开始消费
            return doHandleMsgAndUpdateStatus(message, messageId, consumeService);
        } else {
            // 有消费过/中的,做对应策略处理
            String val = redisPersist.getConsumeStatus(messageId);
            if (ConsumeStatusEnum.CONSUME_STATUS_CONSUMING.getValue().equals(val)) {
                //正在消费中，稍后重试
                log.warn("the same message is considered consuming, try consume later messageId : {}", messageId);
                return false;
            } else if (ConsumeStatusEnum.CONSUME_STATUS_CONSUMED.getValue().equals(val)) {
                //证明消费过了，直接消费认为成功
                log.warn("message has been consumed before! messageId : {}", messageId);
                return true;
            } else {
                //非法结果，降级，直接消费
                log.warn("[NOTIFYME] unknown consume result {}, ignore dedup, continue consuming,  messageId : {} ", val, messageId);
                return doHandleMsgAndUpdateStatus(message, messageId, consumeService);
            }
        }
    }

    private boolean doHandleMsgAndUpdateStatus(Message message, String messageId, IConsumeService consumeService) {
        Boolean consumeResult;
        try {
            // 开始消费
            consumeResult = consumeService.consumeMessage(message);
        } catch (Throwable e) {
            //消费失败了，删除这个key
            try {
                redisPersist.delete(messageId);
            } catch (Exception ex) {
                log.error("error when delete dedup record {}", message, ex);
            }
            throw e;
        }
        //没有异常，正常返回的话，判断消费结果
        try {
            if (consumeResult) {
                //标记为这个消息消费过
                log.info("set consume res as CONSUME_STATUS_CONSUMED , {}", messageId);
                redisPersist.markConsumed(messageId, dedupRecordReserveMinutes);
            } else {
                log.info("consume Res is false, try deleting dedup record ,messageId:{} ", messageId);
                //消费失败了，删除这个key
                redisPersist.delete(messageId);
            }
        } catch (Exception e) {
            log.error("消费去重收尾工作异常 {}，忽略异常", messageId, e);
        }
        return consumeResult;
    }
}
