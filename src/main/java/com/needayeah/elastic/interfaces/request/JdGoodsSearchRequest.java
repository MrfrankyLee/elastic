package com.needayeah.elastic.interfaces.request;

import com.needayeah.elastic.common.page.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * @author lixiaole
 * @date 2021/2/3
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JdGoodsSearchRequest extends PageRequest {
    /**
     * id
     */
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
