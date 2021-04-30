package com.needayeah.elastic.controller;

import com.needayeah.elastic.common.Page;
import com.needayeah.elastic.common.Result;
import com.needayeah.elastic.entity.XaHouse;
import com.needayeah.elastic.interfaces.OrderFace;
import com.needayeah.elastic.interfaces.reponse.JdGoodsResponse;
import com.needayeah.elastic.interfaces.request.JdGoodsSearchRequest;
import com.needayeah.elastic.interfaces.request.XaHousesSearchRequest;
import com.needayeah.elastic.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author lixiaole
 * @date 2021/2/3
 */
@RestController
@RequestMapping("/api/order")
public class OrderController implements OrderFace {

    @Autowired
    private OrderService orderService;

    @Override
    public Result<String> initJDGoodsForES(@RequestParam("keyWord") String keyWord) {
        return orderService.initJDGoodsForES(keyWord);
    }

    @Override
    public Result<Page<JdGoodsResponse>> searchJdGoods(@RequestBody JdGoodsSearchRequest jdGoodsSearchRequest) {
        return orderService.searchJdGoods(jdGoodsSearchRequest);
    }

    @Override
    public Result<String> initXaHouseForES(@RequestParam("count") Integer count) {
        return orderService.initXaHouseForES(count);
    }

    @Override
    public Result<Page<XaHouse>> searchXaHouse(@RequestBody XaHousesSearchRequest request) {
        return orderService.searchXaHouse(request);
    }
}
