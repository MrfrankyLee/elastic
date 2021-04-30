package com.needayeah.elastic.interfaces.request;

import com.needayeah.elastic.common.page.PageRequest;
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
public class XaHousesSearchRequest extends PageRequest {

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
    private Double areaStart;

    /**
     * 面积
     */
    private Double areaEnd;

    /**
     * 层高
     */
    private String storeyHeight;

    /**
     * 朝向
     */
    private String towards;

    /**
     * 建设年限开始时间
     */
    private Integer buildYearStart;

    /**
     * 建设年限结束
     */
    private Integer buildYearEnd;

    /**
     * 地址
     */
    private String address;

    /**
     * 总价最低价
     */
    private Double totalPriceStart;

    /**
     * 总价最高价
     */
    private Double totalPriceEnd;


    /**
     * 单价最低
     */
    private Double unitPriceStart;

    /**
     * 单价最高
     */
    private Double unitPriceEnd;
}
