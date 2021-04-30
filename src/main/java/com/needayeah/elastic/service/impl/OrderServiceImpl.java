package com.needayeah.elastic.service.impl;

import com.needayeah.elastic.common.Page;
import com.needayeah.elastic.common.Pair;
import com.needayeah.elastic.common.Result;
import com.needayeah.elastic.common.utils.BeanUtils;
import com.needayeah.elastic.common.utils.HtmlParseUtil;
import com.needayeah.elastic.domain.JdGoodsSearchDomain;
import com.needayeah.elastic.domain.XaHouseEsDomain;
import com.needayeah.elastic.entity.JdGoods;
import com.needayeah.elastic.entity.XaHouse;
import com.needayeah.elastic.interfaces.reponse.JdGoodsResponse;
import com.needayeah.elastic.interfaces.request.JdGoodsSearchRequest;
import com.needayeah.elastic.interfaces.request.XaHousesSearchRequest;
import com.needayeah.elastic.model.JdGoodsSearchBO;
import com.needayeah.elastic.model.XaHouseSearchBO;
import com.needayeah.elastic.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lixiaole
 * @date 2021/2/3
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private static final int SEARCH_TRADE_ORDER_MAX_NUM = 1000;

    @Autowired
    private JdGoodsSearchDomain jdGoodsSearchDomain;

    @Autowired
    private XaHouseEsDomain xaHouseEsDomain;

    @Autowired
    private HtmlParseUtil htmlParseUtil;

    @Override
    public Result<String> initJDGoodsForES(String keyWord) {
        List<JdGoods> jdGoodsList = htmlParseUtil.parseJdGoods(keyWord);
        boolean flag = jdGoodsSearchDomain.saveOrUpdateJdGoods(jdGoodsList);
        return flag ? Result.success("搞定") : Result.error(10001, "失败");
    }

    @Override
    public Result<Page<JdGoodsResponse>> searchJdGoods(JdGoodsSearchRequest request) {
        if (request.getPageSize() > SEARCH_TRADE_ORDER_MAX_NUM) {
            return Result.error(4000, "查询数量超限");
        }
        Pair<Long, List<JdGoods>> searchPair = jdGoodsSearchDomain.search(BeanUtils.reqTransform(JdGoodsSearchBO.class, request), true);
        return Result.success(Page.of(searchPair.getLeft().intValue(),
                BeanUtils.batchTransform(JdGoodsResponse.class, searchPair.getRight(), true, BeanUtils.TransformEnumType.VALUE_TO_ENUM)));
    }

    @Override
    public Result<String> initXaHouseForES(Integer count) {
        List<XaHouse> xaHouseList = htmlParseUtil.parseHouse(count);
        boolean flag = xaHouseEsDomain.saveOrUpdateXaHouses(xaHouseList);
        return flag ? Result.success("搞定") : Result.error(10001, "失败");
    }

    @Override
    public Result<Page<XaHouse>> searchXaHouse(XaHousesSearchRequest request) {
        if (request.getPageSize() > SEARCH_TRADE_ORDER_MAX_NUM) {
            return Result.error(4000, "查询数量超限");
        }
        Pair<Long, List<XaHouse>> searchPair = xaHouseEsDomain.search(BeanUtils.reqTransform(XaHouseSearchBO.class, request), true);
        return Result.success(Page.of(searchPair.getLeft().intValue(), searchPair.getRight()));
    }
}
