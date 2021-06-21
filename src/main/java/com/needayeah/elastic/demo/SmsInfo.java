package com.needayeah.elastic.demo;

/**
 * @author lixiaole
 * @date 2021/6/9
 */
public class SmsInfo {

    /**
     * 短信服务商的请求URL
     */
    private String url;

    /**
     * 短信内容最多多少个字节
     */
    private Long maxSizeInBytes;


    public SmsInfo(String url, Long maxSizeInBytes) {
        this.url = url;
        this.maxSizeInBytes = maxSizeInBytes;
    }
}
