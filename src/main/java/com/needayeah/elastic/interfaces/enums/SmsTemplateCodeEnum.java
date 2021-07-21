package com.needayeah.elastic.interfaces.enums;

/**
 * 短信模板
 *
 * @author lixiaole
 * @date 2021/4/21
 */
public enum SmsTemplateCodeEnum {

    TEMPLATE_CODE_ENUM("SMS_176914249", "短信模板编码说明");

    /**
     * 模板值
     */
    private String value;

    /**
     * 描述
     */
    private String desc;

    SmsTemplateCodeEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getShowDescription() {
        return this.desc;
    }


    public String getValue() {
        return this.value;
    }
}
