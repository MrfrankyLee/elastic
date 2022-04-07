package com.needayeah.elastic.domain;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.needayeah.elastic.common.page.Pair;
import com.needayeah.elastic.common.utils.BeanUtils;
import com.needayeah.elastic.common.utils.FileUtils;
import com.needayeah.elastic.config.es.EsIndexConfig;
import com.needayeah.elastic.entity.XaHouse;
import com.needayeah.elastic.model.XaHouseSearchBO;
import com.sun.istack.internal.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author lixiaole
 * @date 2021/4/28
 */
@Component
@Slf4j
public class XaHouseEsDomain implements EsIndexConfig {

    public static final String ES_ID = "_id";

    @Value("${xaHouse.es.index:xa_house_index}")
    private String esIndex;

    private static final String INDEX_MAPPING_FILE = "/templates/xa_house_index_mapping.json";

    private static final String INDEX_SETTING_FILE = "/templates/xa_house_index_setting.json";


    @Resource
    private RestHighLevelClient restHighLevelClient;


    @Override
    public String getIndexName() {
        return this.esIndex;
    }

    @Override
    public String getIndexMappingSource() {
        return FileUtils.readContent(INDEX_MAPPING_FILE);
    }

    @Override
    public String getIndexSettingSource() {
        return FileUtils.readContent(INDEX_SETTING_FILE);
    }

    public boolean refresh() {
        RefreshRequest request = new RefreshRequest(esIndex);
        try {
            RefreshResponse response = restHighLevelClient.indices().refresh(request, RequestOptions.DEFAULT);
            return response.getFailedShards() == 0;
        } catch (Exception e) {
            log.error("xa house es refresh failed cause is:{}", e.getMessage(), e);
        }
        return false;
    }


    public boolean saveOrUpdateXaHouses(List<XaHouse> xaHouseList) {
        BulkRequest bulkRequest = new BulkRequest(esIndex);
        for (XaHouse house : xaHouseList) {
            IndexRequest request = new IndexRequest(esIndex)
                    .id(house.getId())
                    .source(JSON.toJSONString(house), XContentType.JSON);
            bulkRequest.add(request);
        }
        try {
            BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            refresh();
            return !response.hasFailures();
        } catch (Exception e) {
            log.error("batch save or update xa house to es failed, cause is:", e);
        }
        return false;
    }

    @NotNull
    public Pair<Long, List<XaHouse>> search(XaHouseSearchBO condition, boolean pageQuery) {
        SearchRequest searchRequest = new SearchRequest(esIndex);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(getBoolQueryBuilder(condition))
                .trackTotalHits(true);
        if (pageQuery && condition.getPageSize() != 0) {
            sourceBuilder.sort(BeanUtils.getBeanFieldName(XaHouse::getBuildYear), SortOrder.DESC);
            sourceBuilder.sort(ES_ID);
            sourceBuilder.from(condition.getPageFrom()).size(condition.getPageSize());
        } else {
            sourceBuilder.size(2000);
        }
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<XaHouse> resultResponses = Lists.newArrayList();
            for (SearchHit hit : response.getHits().getHits()) {
                XaHouse bo = JSON.parseObject(hit.getSourceAsString(), XaHouse.class);
                resultResponses.add(bo);
            }
            return Pair.of(response.getHits().getTotalHits().value, resultResponses);
        } catch (IOException e) {
            log.error("search refund order from es failed, cause is:{}", e.getMessage(), e);
        }
        return Pair.of(0L, Lists.newArrayList());
    }

    private BoolQueryBuilder getBoolQueryBuilder(XaHouseSearchBO condition) {
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        if (!Strings.isEmpty(condition.getId())) {
            queryBuilder.must(QueryBuilders.termQuery(BeanUtils.getBeanFieldName(XaHouse::getId),condition.getId()));
        }
        if (!ObjectUtils.isEmpty(condition.getIds())) {
            queryBuilder.must(QueryBuilders.termsQuery(BeanUtils.getBeanFieldName(XaHouse::getId),condition.getIds()));
        }
        if (!Strings.isEmpty(condition.getTitle())) {
            queryBuilder.must(QueryBuilders.wildcardQuery(BeanUtils.getBeanFieldName(XaHouse::getTitle) + ".keyword", "*" + condition.getTitle() + "*"));
        }
        if (!Strings.isEmpty(condition.getUnitType())) {
            queryBuilder.must(QueryBuilders.wildcardQuery(BeanUtils.getBeanFieldName(XaHouse::getUnitType) + ".keyword", "*" + condition.getUnitType() + "*"));
        }
        if (condition.getAreaStart() != null && condition.getAreaEnd() != null) {
            queryBuilder.must(QueryBuilders.rangeQuery(BeanUtils.getBeanFieldName(XaHouse::getArea))
                    .gte(condition.getAreaStart())
                    .lte(condition.getAreaEnd()));
        }
        if (!Strings.isEmpty(condition.getStoreyHeight())) {
            queryBuilder.must(QueryBuilders.wildcardQuery(BeanUtils.getBeanFieldName(XaHouse::getStoreyHeight) + ".keyword", "*" + condition.getStoreyHeight() + "*"));
        }
        if (!Strings.isEmpty(condition.getTowards())) {
            queryBuilder.must(QueryBuilders.wildcardQuery(BeanUtils.getBeanFieldName(XaHouse::getTowards) + ".keyword", "*" + condition.getTowards() + "*"));
        }
        if (condition.getBuildYearStart() != null && condition.getBuildYearEnd() != null) {
            queryBuilder.must(QueryBuilders.rangeQuery(BeanUtils.getBeanFieldName(XaHouse::getBuildYear))
                    .gte(condition.getBuildYearStart())
                    .lte(condition.getBuildYearEnd()));
        }
        if (!Strings.isEmpty(condition.getAddress())) {
            queryBuilder.must(QueryBuilders.wildcardQuery(BeanUtils.getBeanFieldName(XaHouse::getAddress) + ".keyword", "*" + condition.getAddress() + "*"));
        }
        if (condition.getTotalPriceStart() != null && condition.getTotalPriceEnd() != null) {
            queryBuilder.must(QueryBuilders.rangeQuery(BeanUtils.getBeanFieldName(XaHouse::getTotalPrice))
                    .gte(condition.getTotalPriceStart())
                    .lte(condition.getTotalPriceEnd()));
        }
        if (condition.getUnitPriceStart() != null && condition.getUnitPriceEnd() != null) {
            queryBuilder.must(QueryBuilders.rangeQuery(BeanUtils.getBeanFieldName(XaHouse::getUnitPrice))
                    .gte(condition.getUnitPriceStart())
                    .lte(condition.getUnitPriceEnd()));
        }
        return queryBuilder;
    }
}
