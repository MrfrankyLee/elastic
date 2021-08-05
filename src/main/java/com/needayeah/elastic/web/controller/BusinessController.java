package com.needayeah.elastic.web.controller;

import com.needayeah.elastic.config.mq.BusinessMsgProducer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author lixiaole
 * @date 2021/5/10
 */
@Slf4j
@RestController
@Api(tags = "业务控制层")
public class BusinessController {

    @Autowired
    private BusinessMsgProducer producer;

    @GetMapping("/send")
    @ApiOperation(value = "发送普通消息")
    public void sendMsg(@ApiParam(value = "消息内容", name = "msg", required = true) String msg) {
        producer.sendMsg(msg);
    }

    @GetMapping("/sendDelayFromMsgTtl")
    @ApiOperation(value = "发送普通消息")
    public void sendDelayFromMsgTtl(@ApiParam(value = "消息内容", name = "msg", required = true) String msg,
                        @ApiParam(value = "过期时间", name = "expiration", required = true) String expiration) {
        producer.sendDelayFromMsgTtl(msg, expiration);
    }


    @GetMapping("/sendDelayMsgFromPlugin")
    @ApiOperation(value = "通过插件发送延时消息")
    public void sendDelayMsgFromPlugin(@ApiParam(value = "消息内容", name = "msg", required = true) String msg,
                                       @ApiParam(value = "延长时间", name = "delayTime", required = true) Integer delayTime) {
        log.info("当前时间：{},收到请求，msg:{},delayTime:{}", new Date(), msg, delayTime);
        producer.sendDelayMsgFromPlugin(msg, delayTime);
    }
}
