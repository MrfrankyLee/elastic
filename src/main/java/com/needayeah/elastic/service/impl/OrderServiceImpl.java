package com.needayeah.elastic.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.needayeah.elastic.common.Page;
import com.needayeah.elastic.common.utils.BeanUtils;
import com.needayeah.elastic.common.Result;
import com.needayeah.elastic.common.utils.HtmlParseUtil;
import com.needayeah.elastic.domain.OrderDomain;
import com.needayeah.elastic.entity.JdGoods;
import com.needayeah.elastic.entity.Order;
import com.needayeah.elastic.interfaces.reponse.JdGoodsResponse;
import com.needayeah.elastic.interfaces.reponse.OrderSearchResponse;
import com.needayeah.elastic.interfaces.request.OrderSearchRequest;
import com.needayeah.elastic.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author lixiaole
 * @date 2021/2/3
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    private static final String ORDER_INDEX = "order_index";
    private static final String JD_GOODS = "jd_goods_index";

    @Autowired
    private OrderDomain orderDomain;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private HtmlParseUtil htmlParseUtil;

    @Override
    public Result<Page<OrderSearchResponse>> searchByRequest(OrderSearchRequest request) {
        SearchRequest searchRequest = new SearchRequest(ORDER_INDEX);
        SearchSourceBuilder builder = new SearchSourceBuilder()
                .query(getBuildQuery(request))
                .trackTotalHits(true);
        builder.from(request.getPageNo());
        builder.size(request.getPageSize() == 0 ? 100 : request.getPageSize());
        searchRequest.source(builder);
        List<OrderSearchResponse> responseList = Lists.newArrayList();
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            for (SearchHit hit : response.getHits().getHits()) {
                OrderSearchResponse searchResponse = JSON.parseObject(hit.getSourceAsString(), OrderSearchResponse.class);
                responseList.add(searchResponse);
            }
            Page<OrderSearchResponse> pageResult = Page.of(response.getHits().getTotalHits().value, responseList);
            return Result.success(pageResult);
        } catch (IOException e) {
            log.error("es search data error : ", e.getMessage());
        }
        return Result.error(1, "查询失败");
    }

    @Override
    public Result<String> initOrderForES(int from, int size) {
        List<Order> list = orderDomain.initOrderForES(from, size);

        if (ObjectUtils.isEmpty(list)) {
            return Result.error(0, "查找不到数据");
        }
        try {
            BulkRequest bulkRequest = new BulkRequest(ORDER_INDEX);
            for (Order order : list) {
                bulkRequest.add(new IndexRequest()
                        .id(order.getId())
                        .source(JSON.toJSONString(order), XContentType.JSON));
            }
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("initOrderForES fail : ", e);
        }
        return Result.success("搞定");
    }

    @Override
    public Result<String> initJDGoodsForES(String keyWord) {
        try {
            List<JdGoods> jdGoodsList = htmlParseUtil.parseJdGoods(keyWord);
            BulkRequest bulkRequest = new BulkRequest(JD_GOODS);
            for (JdGoods jdGoods : jdGoodsList) {
                bulkRequest.add(new IndexRequest()
                        .source(JSON.toJSONString(jdGoods), XContentType.JSON));
            }
            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("initJDGoodsForES fail ,keyWord is : " + keyWord, e);
        }
        return Result.success("搞定");
    }

    @Override
    public Result<Page<JdGoodsResponse>> searchJdGoods(String keyWord) {
        SearchRequest searchRequest = new SearchRequest(JD_GOODS);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.trackTotalHits(true).query(getSearchBuild(keyWord));
        sourceBuilder.from(0);
        sourceBuilder.size(5000);
        searchRequest.source(sourceBuilder);
        List<JdGoodsResponse> list = Lists.newArrayList();
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                list.add(JSON.parseObject(hit.getSourceAsString(), JdGoodsResponse.class));
            }
            Page<JdGoodsResponse> pageResult = Page.of(searchResponse.getHits().getTotalHits().value, list);
            return Result.success(pageResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.error(1, "查询失败");
    }

    private QueryBuilder getSearchBuild(String keyWord) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(BeanUtils.getBeanFieldName(JdGoods::getGoodsName), keyWord));
        return boolQueryBuilder;
    }


    private BoolQueryBuilder getBuildQuery(OrderSearchRequest request) {
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        if (StringUtils.hasLength(request.getReceiverName())) {
            queryBuilder.must(QueryBuilders.termQuery(BeanUtils.getBeanFieldName(OrderSearchRequest::getReceiverName), request.getReceiverName()));
        }
        if (StringUtils.hasLength(request.getReceiverProvince())) {
            queryBuilder.must(QueryBuilders.termQuery(BeanUtils.getBeanFieldName(OrderSearchRequest::getReceiverProvince), request.getReceiverProvince()));
        }
        if (StringUtils.hasLength(request.getReceiverCity())) {
            queryBuilder.must(QueryBuilders.termQuery(BeanUtils.getBeanFieldName(OrderSearchRequest::getReceiverCity), request.getReceiverCity()));
        }
        if (StringUtils.hasLength(request.getReceiverDistrict())) {
            queryBuilder.must(QueryBuilders.termQuery(BeanUtils.getBeanFieldName(OrderSearchRequest::getReceiverDistrict), request.getReceiverDistrict()));
        }
        if (StringUtils.hasLength(request.getReceiverAddress())) {
            queryBuilder.must(QueryBuilders.matchPhraseQuery(BeanUtils.getBeanFieldName(OrderSearchRequest::getReceiverAddress), request.getReceiverAddress()));
        }
        return queryBuilder;
    }
}
