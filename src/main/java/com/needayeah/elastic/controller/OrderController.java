package com.needayeah.elastic.controller;

import com.needayeah.elastic.common.Page;
import com.needayeah.elastic.common.Result;
import com.needayeah.elastic.interfaces.OrderFace;
import com.needayeah.elastic.interfaces.reponse.JdGoodsResponse;
import com.needayeah.elastic.interfaces.reponse.OrderSearchResponse;
import com.needayeah.elastic.interfaces.request.OrderSearchRequest;
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
    public Result<Page<OrderSearchResponse>> searchByRequest(@RequestBody OrderSearchRequest request) {
        return orderService.searchByRequest(request);
    }

    @Override
    public Result<String> initOrderForES(@RequestParam("from") int from , @RequestParam("size") int size) {
        return orderService.initOrderForES(from,size);
    }

    @Override
    public Result<String> initJDGoodsForES(String keyWord) {
        return orderService.initJDGoodsForES(keyWord);
    }

    @Override
    public Result<Page<JdGoodsResponse>> searchJdGoods(String keyWord) {
        return orderService.searchJdGoods(keyWord);
    }
}
