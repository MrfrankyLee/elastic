package com.needayeah.elastic;

import com.alibaba.fastjson.JSON;
import com.needayeah.elastic.config.es.EsIndexConfig;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;

@SpringBootTest
class ElasticApplicationTests implements EsIndexConfig {
	@Autowired
	private RestHighLevelClient restHighLevelClient;

	@Test
	void createIndex() throws IOException {
		CreateIndexRequest request = new CreateIndexRequest("order_index");
		CreateIndexResponse response = restHighLevelClient.indices().create(request,RequestOptions.DEFAULT);
		System.out.println(response.index());
	}

	@Test
	void contextLoads() throws IOException {
		GetRequest request = new GetRequest("test_index","1");
		GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
		System.out.println(response.getSourceAsString());
	}

	@Test
	void addTest() throws IOException {
		IndexRequest request = new IndexRequest("test_index");
		request.id("2");
		Map<String,Object> map = new HashMap<>();
		map.put("name","franky");
		map.put("age",29);
		request.source(JSON.toJSONString(map), XContentType.JSON);
		IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
		System.out.println(response.toString());
	}

	@Test
	void searchTest() throws IOException {
		SearchRequest request = new SearchRequest("twitter");
		SearchSourceBuilder builder = new SearchSourceBuilder();
		builder.query(getBoolQueryBuilder());
		builder.trackTotalHits(true);
		request.source(builder);
		SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
		for (SearchHit hit : response.getHits().getHits()) {
			System.out.println(hit.getSourceAsString());
		}
		System.out.println(response.toString());
	}

	private BoolQueryBuilder getBoolQueryBuilder(){
		BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
		queryBuilder.filter(QueryBuilders.rangeQuery("age").gt(3));
		return queryBuilder;
	}

    @Override
    public String getIndexName() {
        return "twitter";
    }

    @Override
    public String getIndexMappingSource() {
        return "null";
    }

    @Override
    public String getIndexSettingSource() {
        return "";
    }
}
