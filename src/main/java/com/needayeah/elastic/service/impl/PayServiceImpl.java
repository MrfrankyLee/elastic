package com.needayeah.elastic.service.impl;

import com.needayeah.elastic.common.pay.IPayment;
import com.needayeah.elastic.entity.Order;
import com.needayeah.elastic.interfaces.reponse.PayResultResponse;
import com.needayeah.elastic.interfaces.request.PayRequest;
import com.needayeah.elastic.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author lixiaole
 * @date 2021/5/18
 */
@Slf4j
@Service

public class PayServiceImpl implements PayService {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public PayResultResponse pay(PayRequest request) {


        Order order = getOrder(request.getOrderId());

        // 根据支付类型获取对应的策略 bean
        IPayment payment = applicationContext.getBean(request.getPaymentType(), IPayment.class);

        // 开始支付
        PayResultResponse payResult = payment.pay(order);

        return payResult;
    }

    private Order getOrder(String orderId) {
        return Order.builder()
                .orderId(orderId)
                .totalAmount(BigDecimal.TEN)
                .goodsName("iphoneX")
                .info("bigger than bigger")
                .build();
    }
}
