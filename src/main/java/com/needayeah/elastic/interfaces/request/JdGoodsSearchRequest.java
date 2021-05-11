package com.needayeah.elastic.interfaces.request;

import com.needayeah.elastic.common.page.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("京东商品查询请求类")
public class JdGoodsSearchRequest extends PageRequest {

    @ApiModelProperty("商品ID")
    private String id;

    @ApiModelProperty("店铺名称")
    private List<String> shopNames;

    @ApiModelProperty("商品名称")
    private String goodsName;

    @ApiModelProperty("最小价格")
    private Double priceStart;

    @ApiModelProperty("最大价格")
    private Double priceEnd;
}
