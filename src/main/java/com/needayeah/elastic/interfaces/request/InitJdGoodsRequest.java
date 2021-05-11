package com.needayeah.elastic.interfaces.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author lixiaole
 * @date 2021/5/8
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "初始化商品请求类")
public class InitJdGoodsRequest implements Serializable {
    private static final long serialVersionUID = 2151029732890489715L;

    @ApiModelProperty(value = "关键字", required = true)
    private String keyWord;
}
