package com.needayeah.elastic.interfaces;

import com.needayeah.elastic.common.utils.Result;
import com.needayeah.elastic.interfaces.request.SendSmsMessageRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author sms短信发送
 */
public interface SmsNotifyFacade {

    /**
     * 短信发送接口
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/sendSmsMessage")
    Result<Boolean> sendSmsMessage(@RequestBody SendSmsMessageRequest request);


}
