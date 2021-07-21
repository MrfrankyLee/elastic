package com.needayeah.elastic.service;

import com.needayeah.elastic.common.utils.Result;
import com.needayeah.elastic.interfaces.request.SendSmsMessageRequest;

/**
 * @author lixiaole
 * @date 2021/7/21
 * @desc 短信发送方发送接口
 */
public interface SmsSenderService {

    /**
     * 发送短信消息
     *
     * @param request 短信请求入参
     * @return
     */
    Result<Boolean> sendSmsMessage(SendSmsMessageRequest request);
}
