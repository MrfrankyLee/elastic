package com.needayeah.elastic.service.impl;


import com.needayeah.elastic.common.page.Page;
import com.needayeah.elastic.common.page.Pair;
import com.needayeah.elastic.common.utils.BeanUtils;
import com.needayeah.elastic.common.utils.HtmlParseUtil;
import com.needayeah.elastic.common.utils.Result;
import com.needayeah.elastic.config.queue.QueueHelper;
import com.needayeah.elastic.domain.JdGoodsSearchDomain;
import com.needayeah.elastic.entity.JdGoods;
import com.needayeah.elastic.interfaces.reponse.JdGoodsResponse;
import com.needayeah.elastic.interfaces.request.JdGoodsSearchRequest;
import com.needayeah.elastic.model.JdGoodsSearchBO;
import com.needayeah.elastic.service.JdGoodsService;
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
public class JdGoodsServiceImpl implements JdGoodsService {

    private static final int SEARCH_TRADE_ORDER_MAX_NUM = 1000;

    @Autowired
    private JdGoodsSearchDomain jdGoodsSearchDomain;

    @Autowired
    private HtmlParseUtil htmlParseUtil;

    @Autowired
    private QueueHelper queueHelper;

    @Override
    public Result<String> initJDGoodsForES(String keyWord) {
        List<JdGoods> jdGoodsList = htmlParseUtil.parseJdGoods(keyWord);
        jdGoodsList.forEach(jdGoods -> {
            queueHelper.asyncExecute(() -> {
                System.out.println(jdGoods);
            });
        });
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

}
