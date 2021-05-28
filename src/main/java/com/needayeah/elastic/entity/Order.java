package com.needayeah.elastic.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author lixiaole
 * @date 2021/5/18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    private String orderId;

    private String goodsName;

    private BigDecimal totalAmount;

    private String info;

    private int OrderStatus;

}
