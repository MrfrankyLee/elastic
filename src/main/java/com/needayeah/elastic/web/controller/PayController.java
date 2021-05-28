package com.needayeah.elastic.web.controller;

import com.needayeah.elastic.interfaces.reponse.PayResultResponse;
import com.needayeah.elastic.interfaces.request.PayRequest;
import com.needayeah.elastic.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lixiaole
 * @date 2021/5/18
 */
@RestController
public class PayController {

    @Autowired
    private PayService payService;

    @GetMapping("/pay")
    public PayResultResponse pay(@RequestParam("orderId") String orderId,
                                 @RequestParam("paymentType") String paymentType) {
        return payService.pay(PayRequest.builder().orderId(orderId).paymentType(paymentType).build());
    }

}
