package com.needayeah.elastic.config.mq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

/**
 * @author lixiaole
 * @date 2021/5/10
 */
@Slf4j
@Component
public class BusinessMsgConsumer {

    @Autowired
    private DuplicateMessageHandler duplicateMessageHandler;

    @RabbitListener(queues = RabbitMQConfig.BUSINESS_QUEUEA_NAME)
    public void receiveA(Message message, Channel channel) throws IOException {
        log.info("收到业务消息A：{}", new String(message.getBody()) + "-----" + message.getMessageProperties().getMessageId());
        boolean hasConsumeFail = duplicateMessageHandler.handleMsgInner(message, msg -> true);
        if (hasConsumeFail) {
            //消费成功
            log.info("consume [{}] msg all successfully", message.getMessageProperties().getMessageId());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } else {
            //消费失败
            log.warn("consume [{}] msg(s) fails, ackIndex = [{}] ", message.getMessageProperties().getMessageId());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.BUSINESS_QUEUEB_NAME)
    public void receiveB(Message message, Channel channel) throws IOException {
        log.info("收到业务消息B：" + new String(message.getBody()) + " ------messageId:" + message.getMessageProperties().getMessageId());
        boolean hasConsumeFail = duplicateMessageHandler.handleMsgInner(message, msg -> true);
        if (hasConsumeFail) {
            //消费成功
            log.info("consume [{}] msg all successfully", message.getMessageProperties().getMessageId());
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } else {
            //消费失败
            log.warn("consume [{}] msg(s) fails, ackIndex = [{}] ", message.getMessageProperties().getMessageId());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    /**
     * 延时消息消费
     *
     * @param message
     * @param channel
     * @throws IOException
     */
    @RabbitListener(queues = RabbitMQConfig.DELAYED_QUEUE_NAME)
    public void receiveDelayMessage(Message message, Channel channel) throws IOException {
        String msg = new String(message.getBody());
        log.info("当前时间：{},延时队列收到消息：{}", new Date().toString(), msg);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
