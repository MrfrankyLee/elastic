package com.needayeah.elastic.domain;

import com.needayeah.elastic.common.utils.Result;
import com.needayeah.elastic.interfaces.enums.SmsSenderEnum;
import com.needayeah.elastic.interfaces.request.SendSmsMessageRequest;

/**
 * @author lixiaole
 * @date 2021/7/21
 * @desc 短信发送方发送接口
 */
public interface SmsSenderInvokeStrategy {

    /**
     * 获取支持的发送方
     *
     * @return
     */
    SmsSenderEnum[] getSender();

    /**
     * 发送短信消息
     *
     * @param request 短信请求入参
     * @return
     */
    Result<Boolean> sendSmsMessage(SendSmsMessageRequest request);
}
