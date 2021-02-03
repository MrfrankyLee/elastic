package com.needayeah.elastic.mapper;

import com.needayeah.elastic.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lixiaole
 * @date 2021/2/3
 */
@Mapper
public interface OrderMapper {

    /**
     * 查询所有的订单
     *
     * @return
     */
    List<Order> searchOrder(@Param("from") int from, @Param("size") int size);
}
