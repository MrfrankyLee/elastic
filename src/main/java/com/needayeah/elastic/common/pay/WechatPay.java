package com.needayeah.elastic.common.pay;

import com.needayeah.elastic.entity.Order;
import com.needayeah.elastic.interfaces.reponse.PayResultResponse;
import org.springframework.stereotype.Service;

/**
 * 微信支付
 *
 * @author lixiaole
 * @date 2021/5/18
 */
@Service("WechatPay")
public class WechatPay implements IPayment {

    @Override
    public PayResultResponse pay(Order order) {
        return new PayResultResponse("微信支付成功");
    }
}
