package com.needayeah.elastic.config.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 * @author lixiaole
 * @date 2021/5/10
 */
@Component
@Slf4j
public class BusinessMsgProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    private void init() {

        /**
         * 如果消息和队列时可持久化的,那么确认回调会在消息写入磁盘后发出
         */
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            String id = correlationData != null ? correlationData.getId() : "";
            if (ack) {
                //log.info("消息确认成功, id:{}", id);
            } else {
                log.error("消息未成功投递, id:{}, cause:{}", id, cause);
            }
        });
        /**
         * 当mandatory标志位设置为true时，如果exchange根据自身类型和消息routingKey无法找到一个合适的queue存储消息，
         * 那么broker会调用basic.return方法将消息返还给生产者;当mandatory设置为false时，出现上述情况broker会直接将消息丢弃;
         * 通俗的讲，mandatory标志告诉broker代理服务器至少将消息route到一个队列中，否则就将消息return给发送者;
         *
         * 使用延时队列插件 会报消息无法路由。报错：NO_ROUTE {参考：https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/issues/138 }
         */
        rabbitTemplate.setReturnsCallback(returned -> {
            if (!returned.getExchange().contains("delay")) {
                log.info("message: " + new String(returned.getMessage().getBody()) + ", return exchange: " + returned.getExchange() + ", routingKey: "
                        + returned.getRoutingKey() + ", replyCode: " + returned.getReplyCode() + ", replyText: " + returned.getReplyText());
            }

        });
    }


    public void sendMsg(String msg) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setMessageId(UUID.randomUUID().toString());
        messageProperties.setContentType("text/plain");
        messageProperties.setContentEncoding("utf-8");
        Message message = new Message(msg.getBytes(), messageProperties);
        String exchange = RabbitMQConfig.BUSINESS_EXCHANGE_NAME;
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertSendAndReceive(exchange, "", message, correlationData);
    }

    public void sendDelayFromMsgTtl(String msg, String expiration) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setMessageId(UUID.randomUUID().toString());
        messageProperties.setContentType("text/plain");
        messageProperties.setContentEncoding("utf-8");
        messageProperties.setExpiration(expiration);
        Message message = new Message(msg.getBytes(), messageProperties);
        String exchange = "testExchange";
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertSendAndReceive(exchange, "", message, correlationData);
    }

    public void sendDelayMsgFromPlugin(String msg, Integer delayTime) {
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend(RabbitMQConfig.DELAYED_EXCHANGE_NAME, RabbitMQConfig.DELAYED_ROUTING_KEY, msg, a -> {
            a.getMessageProperties().setDelay(delayTime);
            return a;
        }, correlationData);
    }
}
