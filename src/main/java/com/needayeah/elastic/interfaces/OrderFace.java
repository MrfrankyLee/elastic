package com.needayeah.elastic.interfaces;

import com.needayeah.elastic.common.Page;
import com.needayeah.elastic.common.Result;
import com.needayeah.elastic.entity.XaHouse;
import com.needayeah.elastic.interfaces.reponse.JdGoodsResponse;
import com.needayeah.elastic.interfaces.request.JdGoodsSearchRequest;
import com.needayeah.elastic.interfaces.request.XaHousesSearchRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author lixiaole
 * @date 2021/2/3
 */

public interface OrderFace {

    /**
     * 京东商品初始化加载到ES
     *
     * @return
     */
    @PostMapping("/initJDGoodsForES")
    Result<String> initJDGoodsForES(@RequestParam("keyWord") String keyWord);

    /**
     * 根据关键字搜索商品
     *
     * @return
     */
    @PostMapping("/searchJdGoods")
    Result<Page<JdGoodsResponse>> searchJdGoods(@RequestBody JdGoodsSearchRequest request);

    /**
     * 初始化西安房产信息到ES
     *
     * @return
     */
    @PostMapping("/initXaHouseForES")
    Result<String> initXaHouseForES(@RequestParam("count") Integer count);

    /**
     * 根据关键字搜索商品
     *
     * @return
     */
    @PostMapping("/searchXaHouse")
    Result<Page<XaHouse>> searchXaHouse(@RequestBody XaHousesSearchRequest request);

}
