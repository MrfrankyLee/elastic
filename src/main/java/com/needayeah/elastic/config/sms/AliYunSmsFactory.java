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
import com.needayeah.elastic.config.sms.enums.SmsTemplateCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * @author lixiaole
 * @desc 阿里云短信发送平台
 * @date 2021/4/21
 */

@Component
@Slf4j
public class AliYunSmsFactory {
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

    public IAcsClient getIAcsClient() {
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

    /**
     * 短信发送
     *
     * @param phoneNumber      接收手机号
     * @param templateCodeEnum 短信模板枚举
     * @param param            短信内容中需替换数据参数
     */
    public void sendSmsMessage(String phoneNumber, SmsTemplateCodeEnum templateCodeEnum, Map<String, String> param) {
        SendSmsRequest request = new SendSmsRequest();
        request.setSysMethod(MethodType.POST);
        request.setSignName(signName);
        request.setPhoneNumbers(phoneNumber);
        request.setTemplateCode(templateCodeEnum.getValue());
        request.setTemplateParam(JSON.toJSONString(param));
        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse = getIAcsClient().getAcsResponse(request);
        } catch (ClientException e) {
            log.error("sendSmsMessage request fail :", e);
        }
        if (Objects.nonNull(sendSmsResponse) && RESPONSE_OK.equalsIgnoreCase(sendSmsResponse.getCode())) {
            log.info("send sms message success,receiver phone number :{}, sendSmsResponse:{}", phoneNumber, JSON.toJSONString(sendSmsResponse));
        } else {
            log.error("send sms message fail, receiver phone number :{}, sendSmsResponse:{}", phoneNumber, JSON.toJSONString(sendSmsResponse));
        }
    }

}

