package com.needayeah.elastic.interfaces.reponse;

import com.needayeah.elastic.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lixiaole
 * @date 2021/2/3
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderSearchResponse {
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
     * 省编码
     */
    private String receiverProvinceCode;

    /**
     * 城市
     */
    private String receiverCity;


    /**
     * 市编码
     */
    private String receiverCityCode;

    /**
     * 地区
     */
    private String receiverDistrict;


    /**
     * 区编码
     */
    private String receiverDistrictCode;

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

}
