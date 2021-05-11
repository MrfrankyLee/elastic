package com.needayeah.elastic.interfaces.request;

import com.needayeah.elastic.common.page.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author lixiaole
 * @date 2021/2/3
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("房屋搜索实体类")
public class XaHousesSearchRequest extends PageRequest {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("户型")
    private String unitType;

    @ApiModelProperty("最小面积")
    private Double areaStart;

    @ApiModelProperty("最大面积")
    private Double areaEnd;

    @ApiModelProperty("层高")
    private String storeyHeight;

    @ApiModelProperty("朝向")
    private String towards;

    @ApiModelProperty("建设年限开始时间")
    private Integer buildYearStart;

    @ApiModelProperty("建设年限结束")
    private Integer buildYearEnd;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("总价最低价")
    private Double totalPriceStart;

    @ApiModelProperty("总价最高价")
    private Double totalPriceEnd;

    @ApiModelProperty("单价最低价")
    private Double unitPriceStart;

    @ApiModelProperty("单价最高价")
    private Double unitPriceEnd;
}
