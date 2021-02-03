package com.needayeah.elastic.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lixiaole
 * @date 2021-02-03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JdGoods {

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 名称
     */
    private String goodsName;

    /**
     * 价格
     */
    private String price;

    /**
     * 图片
     */
    private String img;
}
