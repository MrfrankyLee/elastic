package com.needayeah.elastic.controller;

import com.needayeah.elastic.common.utils.Result;
import com.needayeah.elastic.common.page.Page;
import com.needayeah.elastic.interfaces.JdGoodsFace;
import com.needayeah.elastic.interfaces.reponse.JdGoodsResponse;
import com.needayeah.elastic.interfaces.request.JdGoodsSearchRequest;
import com.needayeah.elastic.service.JdGoodsService;
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
public class JdGoodsController implements JdGoodsFace {

    @Autowired
    private JdGoodsService jdGoodsService;

    @Override
    public Result<String> initJDGoodsForES(@RequestParam("keyWord") String keyWord) {
        return jdGoodsService.initJDGoodsForES(keyWord);
    }

    @Override
    public Result<Page<JdGoodsResponse>> searchJdGoods(@RequestBody JdGoodsSearchRequest jdGoodsSearchRequest) {
        return jdGoodsService.searchJdGoods(jdGoodsSearchRequest);
    }
}
