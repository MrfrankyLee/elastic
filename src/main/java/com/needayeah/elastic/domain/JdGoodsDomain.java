package com.needayeah.elastic.domain;

import com.needayeah.elastic.dao.JdGoodsMapper;
import com.needayeah.elastic.entity.JdGoods;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author lixiaole
 * @date 2021/5/28
 */
@Component
public class JdGoodsDomain {

    @Resource
    private JdGoodsMapper jdGoodsMapper;

    public int insert(JdGoods jdGoods) {
        return jdGoodsMapper.insertSelective(jdGoods);
    }


}
