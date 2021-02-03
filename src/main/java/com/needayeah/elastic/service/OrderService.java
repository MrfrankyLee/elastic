package com.needayeah.elastic.service;

import com.needayeah.elastic.common.Page;
import com.needayeah.elastic.common.Result;
import com.needayeah.elastic.interfaces.reponse.OrderSearchResponse;
import com.needayeah.elastic.interfaces.request.OrderSearchRequest;

/**
 * @author lixiaole
 * @date 2021/2/3
 */

public interface OrderService {

    Result<Page<OrderSearchResponse>> searchByRequest(OrderSearchRequest request);

    Result<String> initOrderForES(int from, int size);
}
