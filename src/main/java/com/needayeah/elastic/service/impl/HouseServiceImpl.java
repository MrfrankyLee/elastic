package com.needayeah.elastic.service.impl;


import com.needayeah.elastic.common.utils.Result;
import com.needayeah.elastic.common.page.Page;
import com.needayeah.elastic.common.page.Pair;
import com.needayeah.elastic.common.utils.BeanUtils;
import com.needayeah.elastic.common.utils.HtmlParseUtil;
import com.needayeah.elastic.domain.XaHouseEsDomain;
import com.needayeah.elastic.entity.XaHouse;
import com.needayeah.elastic.interfaces.request.XaHousesSearchRequest;
import com.needayeah.elastic.model.XaHouseSearchBO;
import com.needayeah.elastic.service.HouseService;
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
public class HouseServiceImpl implements HouseService {

    private static final int SEARCH_TRADE_ORDER_MAX_NUM = 1000;

    @Autowired
    private XaHouseEsDomain xaHouseEsDomain;

    @Autowired
    private HtmlParseUtil htmlParseUtil;


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
