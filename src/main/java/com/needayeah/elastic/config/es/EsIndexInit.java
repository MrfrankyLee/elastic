package com.needayeah.elastic.config.es;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

/**
 * @author lixiaole
 * @date 2021/4/28
 */
@Component
@Conditional(EsIndexInit.Load.class)
public class EsIndexInit {

    @Autowired(required = false)
    private List<EsIndexConfig> indexConfigs;

    @Autowired(required = false)
    private RestHighLevelClient restHighLevelClient;

    @PostConstruct
    public void init() {
        indexConfigs.forEach(config -> {
            GetIndexRequest getIndexRequest = new GetIndexRequest(config.getIndexName());
            try {
                boolean isExist = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
                if (!isExist) {
                    CreateIndexRequest createIndexRequest = new CreateIndexRequest(config.getIndexName())
                            .mapping(config.getIndexMappingSource(), XContentType.JSON)
                            .settings(config.getIndexSettingSource(), XContentType.JSON);
                    restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static class Load implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return true;
        }
    }
}
