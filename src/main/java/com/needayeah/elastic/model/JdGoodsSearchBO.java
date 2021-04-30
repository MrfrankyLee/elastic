package com.needayeah.elastic.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author lixiaole
 * @date 2021/4/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JdGoodsSearchBO {
    /**
     * 记录开始位置
     */
    private Integer pageFrom;

    private Integer pageSize;

    private String id;

    /**
     * 店铺名称
     */
    private List<String> shopNames;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 起始价格
     */
    private Double priceStart;

    /**
     * 结束时间
     */
    private Double priceEnd;

}
