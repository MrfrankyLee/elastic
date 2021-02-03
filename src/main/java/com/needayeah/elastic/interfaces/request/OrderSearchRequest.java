package com.needayeah.elastic.interfaces.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lixiaole
 * @date 2021/2/3
 */
@NoArgsConstructor
@Data
public class OrderSearchRequest {
    /**
     * id
     */
    private String id;

    /**
     * 收件人
     */
    private String receiverName;

    /**
     * 省份
     */
    private String receiverProvince;

    /**
     * 城市
     */
    private String receiverCity;

    /**
     * 地区
     */
    private String receiverDistrict;

    /**
     * 地址，不包含省市区
     */
    private String receiverAddress;

    /**
     * 收件人手机
     */
    private String receiverMobile;

    /**
     * 收件人电话
     */
    private String receiverTelno;
    /**
     * 当前页
     */
    private int pageNo;

    /**
     * 每页大小
     */
    private int pageSize;
}
