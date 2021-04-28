package com.needayeah.elastic.config.es;

/**
 * @author lixiaole
 * @desc ES索引配置
 * @date 2021/4/28
 */
public interface EsIndexConfig {

    /**
     * 索引名称
     *
     * @return
     */
    String getIndexName();

    /**
     * 静态模板内容
     *
     * @return
     */
    String getIndexMappingSource();

    /**
     * 索引配置内容
     *
     * @return
     */
    String getIndexSettingSource();
}
