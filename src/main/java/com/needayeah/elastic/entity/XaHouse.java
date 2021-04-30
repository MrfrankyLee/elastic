package com.needayeah.elastic.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lixiaole
 * @date 2021/4/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XaHouse implements Serializable {
    private static final long serialVersionUID = 8253139280455638679L;

    /**
     * id
     */
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 户型
     */
    private String unitType;

    /**
     * 面积
     */
    private Double area;

    /**
     * 层高
     */
    private String storeyHeight;

    /**
     * 朝向
     */
    private String towards;

    /**
     * 建设年限
     */
    private Integer buildYear;

    /**
     * 地址
     */
    private String address;

    /**
     * 总价
     */
    private Double totalPrice;


    /**
     * 单价
     */
    private Double unitPrice;
}
