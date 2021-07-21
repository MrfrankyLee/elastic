package com.needayeah.elastic.web.controller;

import com.needayeah.elastic.common.utils.Result;
import com.needayeah.elastic.interfaces.SmsNotifyFacade;
import com.needayeah.elastic.interfaces.request.SendSmsMessageRequest;
import com.needayeah.elastic.service.SmsNotifyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author lixiaole
 * @date 2021/7/21
 */
@RestController
@Slf4j
@RequestMapping("/sms/notify")
@Api(tags = "短信通知管理")
public class SmsNotifyController implements SmsNotifyFacade {

    @Resource
    private SmsNotifyService smsNotifyService;

    @Override
    @ApiOperation("发送短信")
    public Result<Boolean> sendSmsMessage(@RequestBody SendSmsMessageRequest request) {
        return smsNotifyService.sendSmsMessage(request);
    }
}
