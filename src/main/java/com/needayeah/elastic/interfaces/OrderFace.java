package com.needayeah.elastic.interfaces;

import com.needayeah.elastic.common.Page;
import com.needayeah.elastic.common.Result;
import com.needayeah.elastic.interfaces.reponse.JdGoodsResponse;
import com.needayeah.elastic.interfaces.reponse.OrderSearchResponse;
import com.needayeah.elastic.interfaces.request.OrderSearchRequest;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author lixiaole
 * @date 2021/2/3
 */

public interface OrderFace {

    /**
     *  order搜索
     * @param request
     * @return
     */
    @PostMapping("searchByRequest")
    Result<Page<OrderSearchResponse>> searchByRequest(@RequestBody OrderSearchRequest request);

    /**
     * 初始化ES
     * @return
     */
    @PostMapping("initOrderForES")
    Result<String> initOrderForES(@RequestParam("from") int from , @RequestParam("size") int size);

    /**
     * 京东商品初始化加载到ES
     * @return
     */
    @PostMapping("initJDGoodsForES")
    Result<String> initJDGoodsForES(@Param("keyWord") String keyWord);

    /**
     * 根据关键字搜索商品
     * @return
     */
    @PostMapping("searchJdGoods")
    Result<Page<JdGoodsResponse>> searchJdGoods(@Param("keyWord") String keyWord);
}
