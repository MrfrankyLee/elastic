package com.needayeah.elastic.config.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lixiaole
 * @desc rabbitmq消息队列配置
 * @date 2021/5/10
 */
@Configuration
public class RabbitMQConfig {
    public static final String BUSINESS_EXCHANGE_NAME = "dead.letter.demo.simple.business.exchange";
    public static final String BUSINESS_QUEUEA_NAME = "dead.letter.demo.simple.business.queuea";
    public static final String BUSINESS_QUEUEB_NAME = "dead.letter.demo.simple.business.queueb";
    public static final String DEAD_LETTER_EXCHANGE = "dead.letter.demo.simple.deadletter.exchange";
    public static final String DEAD_LETTER_QUEUEA_ROUTING_KEY = "dead.letter.demo.simple.deadletter.queuea.routingkey";
    public static final String DEAD_LETTER_QUEUEB_ROUTING_KEY = "dead.letter.demo.simple.deadletter.queueb.routingkey";
    public static final String DEAD_LETTER_QUEUEA_NAME = "dead.letter.demo.simple.deadletter.queuea";
    public static final String DEAD_LETTER_QUEUEB_NAME = "dead.letter.demo.simple.deadletter.queueb";

    /**
     * 声明业务Exchange
     *
     * @return
     */
    @Bean("businessExchange")
    public FanoutExchange businessExchange() {
        return new FanoutExchange(BUSINESS_EXCHANGE_NAME);
    }

    /**
     * 声明死信Exchange
     *
     * @return
     */
    @Bean("deadLetterExchange")
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    /**
     * 声明业务队列A
     *
     * @return
     */
    @Bean("businessQueueA")
    public Queue businessQueueA() {
        Map<String, Object> args = new HashMap<>(2);
        //x-dead-letter-exchange    这里声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        //x-dead-letter-routing-key  这里声明当前队列的死信路由key
        args.put("x-dead-letter-routing-key", DEAD_LETTER_QUEUEA_ROUTING_KEY);
        return QueueBuilder.durable(BUSINESS_QUEUEA_NAME).withArguments(args).build();
    }

    /**
     * 声明业务队列B
     *
     * @return
     */
    @Bean("businessQueueB")
    public Queue businessQueueB() {
        Map<String, Object> args = new HashMap<>(2);
        //x-dead-letter-exchange    这里声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        //x-dead-letter-routing-key  这里声明当前队列的死信路由key
        args.put("x-dead-letter-routing-key", DEAD_LETTER_QUEUEB_ROUTING_KEY);
        return QueueBuilder.durable(BUSINESS_QUEUEB_NAME).withArguments(args).build();
    }

    /**
     * 声明死信队列A
     *
     * @return
     */
    @Bean("deadLetterQueueA")
    public Queue deadLetterQueueA() {
        return new Queue(DEAD_LETTER_QUEUEA_NAME);
    }

    /**
     * 声明死信队列B
     *
     * @return
     */
    @Bean("deadLetterQueueB")
    public Queue deadLetterQueueB() {
        return new Queue(DEAD_LETTER_QUEUEB_NAME);
    }

    /**
     * 声明业务队列A绑定关系
     *
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding businessBindingA(@Qualifier("businessQueueA") Queue queue,
                                    @Qualifier("businessExchange") FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }

    /**
     * 声明业务队列B绑定关系
     *
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding businessBindingB(@Qualifier("businessQueueB") Queue queue,
                                    @Qualifier("businessExchange") FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }

    /**
     * 声明死信队列A绑定关系
     *
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding deadLetterBindingA(@Qualifier("deadLetterQueueA") Queue queue,
                                      @Qualifier("deadLetterExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(DEAD_LETTER_QUEUEA_ROUTING_KEY);
    }

    /**
     * 声明死信队列B绑定关系
     *
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding deadLetterBindingB(@Qualifier("deadLetterQueueB") Queue queue,
                                      @Qualifier("deadLetterExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(DEAD_LETTER_QUEUEB_ROUTING_KEY);
    }

    @Bean("testExchange")
    public FanoutExchange testExchange() {
        return new FanoutExchange("testExchange", true, false);
    }

    /**
     * 消息变成死信的三种情况：
     * 消息被拒绝（basic.reject/ basic.nack）并且requeue=false
     * 消息TTL过期（参考：RabbitMQ之TTL（Time-To-Live 过期时间））
     * 队列达到最大长度
     *
     * @return
     */
    @Bean("testQueue")
    public Queue testQueue() {
        Map<String, Object> args = new HashMap<>(2);
        //x-dead-letter-exchange    这里声明当前队列绑定的死信交换机
        args.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        //x-dead-letter-routing-key  这里声明当前队列的死信路由key
        args.put("x-dead-letter-routing-key", DEAD_LETTER_QUEUEA_ROUTING_KEY);
        return QueueBuilder.durable("testQueue").withArguments(args).build();
    }

    @Bean("testBinding")
    public Binding testBinding(@Qualifier("testQueue") Queue queue,
                               @Qualifier("testExchange") FanoutExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange);
    }


    /** 以下是基于延时队列插件实现延时消息 */
    public static final String DELAYED_QUEUE_NAME = "delay.queue.demo.delay.queue";
    public static final String DELAYED_EXCHANGE_NAME = "delay.queue.demo.delay.exchange";
    public static final String DELAYED_ROUTING_KEY = "delay.queue.demo.delay.routingkey";

    /**
     * 声明一个延时消息的队列
     *
     * @return
     */
    @Bean
    public Queue immediateQueue() {
        return new Queue(DELAYED_QUEUE_NAME);
    }

    /**
     * 声明一个延时消息的交换机
     *
     * @return
     */
    @Bean
    public CustomExchange customExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(DELAYED_EXCHANGE_NAME, "x-delayed-message", true, false, args);
    }

    @Bean
    public Binding bindingNotify(@Qualifier("immediateQueue") Queue queue,
                                 @Qualifier("customExchange") CustomExchange customExchange) {
        return BindingBuilder.bind(queue).to(customExchange).with(DELAYED_ROUTING_KEY).noargs();
    }
}
