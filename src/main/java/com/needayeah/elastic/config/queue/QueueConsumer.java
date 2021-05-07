package com.needayeah.elastic.config.queue;

/**
 * 队列消费者
 *
 * @param <T>
 */
public interface QueueConsumer<T> {

    /**
     * 消费消息
     * @param t
     */
    void doConsume(T t);

}
