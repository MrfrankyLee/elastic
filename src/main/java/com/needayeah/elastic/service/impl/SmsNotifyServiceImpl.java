package com.needayeah.elastic.service.impl;


import com.needayeah.elastic.common.utils.Result;
import com.needayeah.elastic.domain.SmsSenderInvokeStrategy;
import com.needayeah.elastic.domain.SmsSenderManager;
import com.needayeah.elastic.interfaces.request.SendSmsMessageRequest;
import com.needayeah.elastic.service.SmsNotifyService;
import org.springframework.stereotype.Service;

/**
 * @author lixiaole
 * @date 2021/7/21
 */
@Service
public class SmsNotifyServiceImpl implements SmsNotifyService {

    @Override
    public Result<Boolean> sendSmsMessage(SendSmsMessageRequest request) {

        // 根据请求获取对应的短信发送方
        SmsSenderInvokeStrategy senderInvokeStrategy = SmsSenderManager.getSenderInvoke(request.getSenderEnum());

        return senderInvokeStrategy.sendSmsMessage(request);
    }
}
