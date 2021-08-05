package com.needayeah.elastic.config.mq;

import org.springframework.amqp.core.Message;

/**
 * @author lixiaole
 * @date 2021/8/4
 */
public interface IConsumeService {
    /**
     * 消息消费实现类
     *
     * @param message 消息内容
     * @return
     */
    Boolean consumeMessage(Message message);
}
