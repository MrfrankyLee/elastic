package com.needayeah.elastic.common.pay;

import com.needayeah.elastic.entity.Order;
import com.needayeah.elastic.interfaces.reponse.PayResultResponse;
import org.springframework.stereotype.Service;

/**
 * 银联云闪付
 *
 * @author lixiaole
 * @date 2021/5/18
 */
@Service("UnionPay")
public class UnionPay implements IPayment {

    @Override
    public PayResultResponse pay(Order order) {
        return new PayResultResponse("云闪付支付成功");
    }
}
