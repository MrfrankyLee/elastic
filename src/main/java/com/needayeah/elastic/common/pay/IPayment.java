package com.needayeah.elastic.common.pay;

import com.needayeah.elastic.entity.Order;
import com.needayeah.elastic.interfaces.reponse.PayResultResponse;

/**
 * @author lixiaole
 * @date 2021/5/18
 */
public interface IPayment {
    /**
     * 支付
     *
     * @param order
     * @return
     */
    PayResultResponse pay(Order order);
}
