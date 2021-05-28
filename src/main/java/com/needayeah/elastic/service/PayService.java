package com.needayeah.elastic.service;


import com.needayeah.elastic.interfaces.reponse.PayResultResponse;
import com.needayeah.elastic.interfaces.request.PayRequest;

/**
 * @author lixiaole
 * @date 2021/5/18
 */
public interface PayService {

    PayResultResponse pay(PayRequest payRequest);
}
