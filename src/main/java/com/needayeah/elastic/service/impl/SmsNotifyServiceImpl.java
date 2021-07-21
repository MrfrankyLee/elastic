package com.needayeah.elastic.service.impl;


import com.needayeah.elastic.common.utils.Result;
import com.needayeah.elastic.interfaces.request.SendSmsMessageRequest;
import com.needayeah.elastic.service.SmsNotifyService;
import com.needayeah.elastic.service.SmsSenderService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author lixiaole
 * @date 2021/7/21
 */
@Service
public class SmsNotifyServiceImpl implements SmsNotifyService {

    @Resource
    private ApplicationContext applicationContext;

    @Override
    public Result<Boolean> sendSmsMessage(SendSmsMessageRequest request) {

        // 根据请求获取对应的短信发送方
        SmsSenderService sendService = applicationContext.getBean(request.getSenderEnum().name(), SmsSenderService.class);

        return sendService.sendSmsMessage(request);
    }
}
