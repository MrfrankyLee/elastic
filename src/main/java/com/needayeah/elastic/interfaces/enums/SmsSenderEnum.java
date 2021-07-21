package com.needayeah.elastic.interfaces.enums;


import com.needayeah.elastic.common.enums.TransferEnum;

/**
 * @author lixiaole
 * @date 2021/07/21
 * @desc 短信发送方枚举
 */

public enum SmsSenderEnum implements TransferEnum<String> {


    ALI_YUN_SMS(1, "阿里云短信");

    SmsSenderEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private Integer code;
    private String desc;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String getValue() {
        return desc;
    }
}



