package com.needayeah.elastic.interfaces.request;


import com.needayeah.elastic.interfaces.enums.SmsSenderEnum;
import com.needayeah.elastic.interfaces.enums.SmsTemplateCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


/**
 * @author lixiaole
 * @date 2021/07/21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel("发送sms消息request")
public class SendSmsMessageRequest {

    @ApiModelProperty(value = "手机号码", required = true)
    private String phoneNumber;

    @ApiModelProperty(value = "消息模板", required = true)
    private SmsTemplateCodeEnum templateCodeEnum;

    @ApiModelProperty(value = "短信内容中需替换数据参数", required = true)
    private Map<String, String> param;

    @ApiModelProperty(value = "短信发送方,默认发送方--阿里云")
    private SmsSenderEnum senderEnum = SmsSenderEnum.ALI_YUN_SMS;
}
