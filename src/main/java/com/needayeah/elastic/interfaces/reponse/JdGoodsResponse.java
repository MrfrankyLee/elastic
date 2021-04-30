package com.needayeah.elastic.interfaces.reponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lixiaole
 * @date 2021/2/4
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JdGoodsResponse {

    private String id;
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
    private Double price;

    /**
     * 图片
     */
    private String img;
}
