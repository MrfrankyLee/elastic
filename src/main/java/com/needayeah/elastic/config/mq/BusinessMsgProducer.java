package com.needayeah.elastic.config.mq;

import lombok.extern.slf4j.Slf4j;
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
public class BusinessMsgProducer implements RabbitTemplate.ConfirmCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    private void init() {
        rabbitTemplate.setConfirmCallback(this);
    }


    public void sendMsg(String msg) {
        String exchange = RabbitMQConfig.BUSINESS_EXCHANGE_NAME;
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertSendAndReceive(exchange, "", msg, correlationData);

    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData != null ? correlationData.getId() : "";
        if (ack) {
            log.info("消息确认成功, id:{}", id);
        } else {
            log.error("消息未成功投递, id:{}, cause:{}", id, ack);
        }
    }

    public void sendDelayMsg(String msg, Integer delayTime) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.DELAYED_EXCHANGE_NAME, RabbitMQConfig.DELAYED_ROUTING_KEY, msg, a ->{
            a.getMessageProperties().setDelay(delayTime);
            return a;
        });
    }
}
