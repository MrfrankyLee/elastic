package com.needayeah.elastic.config.mq;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author lixiaole
 * @date 2021/5/10
 */
@Slf4j
@Component
public class DeadLetterMessageReceiver {


    @RabbitListener(queues = RabbitMQConfig.DEAD_LETTER_QUEUEA_NAME)
    public void receiveA(Message message, Channel channel) throws IOException {
        log.info("收到死信消息A：" + new String(message.getBody()));
        log.info("死信消息properties：{}", JSON.toJSONString(message.getMessageProperties()));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    @RabbitListener(queues = RabbitMQConfig.DEAD_LETTER_QUEUEB_NAME)
    public void receiveB(Message message, Channel channel) throws IOException {
        log.info("收到死信消息B：" + new String(message.getBody()));
        log.info("死信消息properties：{}", JSON.toJSONString(message.getMessageProperties()));
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
