package com.needayeah.elastic.domain;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.needayeah.elastic.common.page.Pair;
import com.needayeah.elastic.common.utils.BeanUtils;
import com.needayeah.elastic.common.utils.FileUtils;
import com.needayeah.elastic.config.es.EsIndexConfig;
import com.needayeah.elastic.entity.JdGoods;
import com.needayeah.elastic.model.JdGoodsSearchBO;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author lixiaole
 * @date 2021/4/28
 */
@Component
@Slf4j
public class JdGoodsSearchDomain implements EsIndexConfig {

    public static final String ES_ID = "_id";

    @Value("${JdGoods.es.index:jd_goods_index}")
    private String esIndex;

    private static final String INDEX_MAPPING_FILE = "/templates/jd_goods_index_mapping.json";
    private static final String INDEX_SETTING_FILE = "/templates/jd_goods_index_setting.json";


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
            log.error("Jd goods es refresh failed cause is:{}", e.getMessage(), e);
        }
        return false;
    }


    public boolean saveOrUpdateJdGoods(List<JdGoods> jdGoodsList) {
        BulkRequest bulkRequest = new BulkRequest(esIndex);
        for (JdGoods goods : jdGoodsList) {
            IndexRequest request = new IndexRequest(esIndex)
                    .id(goods.getId())
                    .source(JSON.toJSONString(goods), XContentType.JSON);
            bulkRequest.add(request);
        }
        try {
            BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            return !response.hasFailures();
        } catch (Exception e) {
            log.error("batch update refund order to es failed, cause is:", e);
        }
        return false;
    }

    @NotNull
    public Pair<Long, List<JdGoods>> search(JdGoodsSearchBO condition, boolean pageQuery) {
        SearchRequest searchRequest = new SearchRequest(esIndex);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(getBoolQueryBuilder(condition))
                .trackTotalHits(true);
        if (pageQuery && condition.getPageSize() != 0) {
            sourceBuilder.sort(BeanUtils.getBeanFieldName(JdGoods::getPrice));
            sourceBuilder.sort(ES_ID);
            sourceBuilder.from(condition.getPageFrom()).size(condition.getPageSize());
        } else {
            sourceBuilder.size(2000);
        }
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            List<JdGoods> resultResponses = Lists.newArrayList();
            for (SearchHit hit : response.getHits().getHits()) {
                JdGoods bo = JSON.parseObject(hit.getSourceAsString(), JdGoods.class);
                resultResponses.add(bo);
            }
            return Pair.of(response.getHits().getTotalHits().value, resultResponses);
        } catch (IOException e) {
            log.error("search refund order from es failed, cause is:{}", e.getMessage(), e);
        }
        return Pair.of(0L, Lists.newArrayList());
    }

    private BoolQueryBuilder getBoolQueryBuilder(JdGoodsSearchBO condition) {
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        if (!Strings.isEmpty(condition.getGoodsName())) {
            queryBuilder.must(QueryBuilders.wildcardQuery(BeanUtils.getBeanFieldName(JdGoods::getGoodsName) + ".keyword", "*" + condition.getGoodsName() + "*"));
        }
        if (!CollectionUtils.isEmpty(condition.getShopNames())) {
            queryBuilder.must(QueryBuilders.termsQuery(BeanUtils.getBeanFieldName(JdGoods::getShopName) + ".keyword", condition.getShopNames()));
        }
        if (condition.getPriceStart() != null && condition.getPriceEnd() != null) {
            queryBuilder.must(QueryBuilders.rangeQuery(BeanUtils.getBeanFieldName(JdGoods::getPrice))
                    .gte(condition.getPriceStart())
                    .lte(condition.getPriceEnd()));
        }
        return queryBuilder;
    }
}
