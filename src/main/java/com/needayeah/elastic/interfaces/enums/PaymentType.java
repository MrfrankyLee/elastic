package com.needayeah.elastic.interfaces.enums;

import com.needayeah.elastic.common.enums.DescriptionEnum;
import com.needayeah.elastic.common.enums.TransferEnum;

/**
 * @author lixiaole
 * @date 2021/5/18
 */
public enum PaymentType implements TransferEnum<Integer>, DescriptionEnum {
    /** 枚举 */
    ALI_PC_PAY("AliPcPay", "支付宝Pc支付"),
    AliH5Pay("AliH5Pay", "支付宝H5支付"),
    WechatPay("WechatPay", "微信支付"),
    UnionPay("UnionPay", "银联云闪付");

    private String value;

    private String desc;

    PaymentType(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public String getShowDescription() {
        return null;
    }

    @Override
    public Integer getValue() {
        return null;
    }
}
