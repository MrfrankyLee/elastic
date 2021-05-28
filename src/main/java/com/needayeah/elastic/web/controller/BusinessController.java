package com.needayeah.elastic.web.controller;

import com.google.common.collect.Maps;
import com.needayeah.elastic.config.mq.BusinessMsgProducer;
import com.needayeah.elastic.config.sms.AliYunSmsFactory;
import com.needayeah.elastic.config.sms.enums.SmsTemplateCodeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

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

    @Autowired
    private AliYunSmsFactory aliYunSmsFactory;

    @GetMapping("/send")
    @ApiOperation(value = "发送普通消息")
    public void sendMsg(@ApiParam(value = "消息内容", name = "msg", required = true) String msg) {
        producer.sendMsg(msg);
    }

    @GetMapping("/delayMsg2")
    @ApiOperation(value = "发送延时消息")
    public void delayMsg2(@ApiParam(value = "消息内容", name = "msg", required = true) String msg,
                          @ApiParam(value = "延长时间", name = "delayTime", required = true) Integer delayTime) {
        log.info("当前时间：{},收到请求，msg:{},delayTime:{}", new Date(), msg, delayTime);
        producer.sendDelayMsg(msg, delayTime);
    }

    @GetMapping("/sendSms")
    @ApiOperation(value = "发送普通消息")
    public void sendSMsg(@ApiParam(value = "手机号码", name = "phoneNumber", required = true) String phoneNumber,
                         @ApiParam(value = "验证码", name = "code", required = true) String code) {

        Map<String, String> map = Maps.newHashMap();
        map.put("code", code);
        aliYunSmsFactory.sendSmsMessage(phoneNumber, SmsTemplateCodeEnum.TEMPLATE_CODE_ENUM, map);
    }
}
