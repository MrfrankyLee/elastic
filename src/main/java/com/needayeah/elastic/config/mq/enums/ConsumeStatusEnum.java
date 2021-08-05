package com.needayeah.elastic.config.mq.enums;

/**
 * @author lixiaole
 * @date 2021/8/4
 */
public enum ConsumeStatusEnum {

    CONSUME_STATUS_CONSUMING("CONSUMING", "消费中"),
    CONSUME_STATUS_CONSUMED("CONSUMED", "消费完成");


    private String value;

    private String desc;

    ConsumeStatusEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
