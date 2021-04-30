package com.needayeah.elastic.service;

import com.needayeah.elastic.common.Page;
import com.needayeah.elastic.common.Result;
import com.needayeah.elastic.entity.XaHouse;
import com.needayeah.elastic.interfaces.reponse.JdGoodsResponse;
import com.needayeah.elastic.interfaces.request.JdGoodsSearchRequest;
import com.needayeah.elastic.interfaces.request.XaHousesSearchRequest;

/**
 * @author lixiaole
 * @date 2021/2/3
 */

public interface OrderService {

    Result<String> initJDGoodsForES(String keyWord);

    Result<Page<JdGoodsResponse>> searchJdGoods(JdGoodsSearchRequest jdGoodsSearchRequest);

    Result<String> initXaHouseForES(Integer count);

    Result<Page<XaHouse>> searchXaHouse(XaHousesSearchRequest request);
}
