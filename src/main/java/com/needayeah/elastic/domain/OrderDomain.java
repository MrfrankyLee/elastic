package com.needayeah.elastic.domain;

import com.needayeah.elastic.entity.Order;
import com.needayeah.elastic.mapper.OrderMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lixiaole
 * @date 2021/2/3
 */
@Component
public class OrderDomain {

    @Resource
    private OrderMapper orderMapper;

    public List<Order> initOrderForES(int from, int size) {
        return orderMapper.searchOrder(from, size);
    }
}
