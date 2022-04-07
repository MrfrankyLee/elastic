package com.needayeah.elastic.config.sms;


import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.needayeah.elastic.common.utils.Result;
import com.needayeah.elastic.domain.SmsSenderInvokeStrategy;
import com.needayeah.elastic.interfaces.enums.SmsSenderEnum;
import com.needayeah.elastic.interfaces.request.SendSmsMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author lixiaole
 * @desc 阿里云短信发送平台
 * @date 2021/4/21
 */
@Slf4j
@Component
public class AliYunSmsMessage implements SmsSenderInvokeStrategy {
    /**
     * 阿里云短信服务accessKey 参考 阿里云短信控制
     */
    @Value("${aliYun.sms.accessKeyId:}")
    private String accessKeyId;

    /**
     * 阿里云短信服务accessKeySecret 参考 阿里云短信控制 (需要手机短信验证)
     */
    @Value("${aliYun.sms.accessKeySecret:}")
    private String accessKeySecret;

    /**
     * 短信签名
     */
    @Value("${aliYun.sms.signName:}")
    private String signName;

    private static final String RESPONSE_OK = "OK";

    private static final String regionId = "cn-hangzhou";

    private static final String product = "Dysmsapi";

    private static String domain = "dysmsapi.aliyuncs.com";

    private IAcsClient iAcsClient;

    @Override
    public SmsSenderEnum[] getSender() {
        return new SmsSenderEnum[]{SmsSenderEnum.ALI_YUN_SMS};
    }

    @Bean
    public IAcsClient iAcsClient() {
        if (Objects.isNull(iAcsClient)) {
            IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
            try {
                DefaultProfile.addEndpoint(regionId, product, domain);
            } catch (Exception e) {
                log.error("init IAcsClient fail :", e);
            }
            iAcsClient = new DefaultAcsClient(profile);
        }
        return iAcsClient;
    }

    @Override
    public Result<Boolean> sendSmsMessage(SendSmsMessageRequest request) {
        SendSmsRequest smsRequest = new SendSmsRequest();
        smsRequest.setSysMethod(MethodType.POST);
        smsRequest.setSignName(signName);
        smsRequest.setPhoneNumbers(request.getPhoneNumber());
        smsRequest.setTemplateCode(request.getTemplateCodeEnum().getValue());
        smsRequest.setTemplateParam(JSON.toJSONString(request.getParam()));
        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse = iAcsClient.getAcsResponse(smsRequest);
        } catch (ClientException e) {
            log.error("sendSmsMessage request fail :", e.getMessage());
        }
        if (Objects.nonNull(sendSmsResponse) && RESPONSE_OK.equalsIgnoreCase(sendSmsResponse.getCode())) {
            log.info("send sms message success,receiver phone number :{}, sendSmsResponse:{}", request.getPhoneNumber(), JSON.toJSONString(sendSmsResponse));
            return Result.success(Boolean.TRUE);
        } else {
            log.error("send sms message fail, receiver phone number :{}, sendSmsResponse:{}", request.getPhoneNumber(), JSON.toJSONString(sendSmsResponse));
        }
        return Result.success(Boolean.FALSE);
    }
}

