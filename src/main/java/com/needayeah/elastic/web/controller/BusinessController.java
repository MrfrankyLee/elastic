package com.needayeah.elastic.web.controller;

import com.needayeah.elastic.config.mq.BusinessMsgProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author lixiaole
 * @date 2021/5/10
 */
@Slf4j
@RestController
public class BusinessController {

    @Autowired
    private BusinessMsgProducer producer;

    @RequestMapping("send")
    public void sendMsg(String msg) {
        producer.sendMsg(msg);
    }

    @RequestMapping("delayMsg2")
    public void delayMsg2(String msg, Integer delayTime) {
        log.info("当前时间：{},收到请求，msg:{},delayTime:{}", new Date(), msg, delayTime);
        producer.sendDelayMsg(msg, delayTime);
    }
}
