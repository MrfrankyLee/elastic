package com.needayeah.elastic.demo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lixiaole
 * @date 2021/6/9
 */
public class SmsRouter {

    private static final SmsRouter instance = new SmsRouter();

    private final Map<Integer,SmsInfo> smsInfoRouteMap;

    public SmsRouter() {
        this.smsInfoRouteMap = this.loadSmsInfoRouteMapFromDB();
    }

    private Map<Integer, SmsInfo> loadSmsInfoRouteMapFromDB() {
        // 初始化
        Map<Integer,SmsInfo> routeMap = new HashMap<>();
        routeMap.put(1,new SmsInfo("https://www.aliyun.com",180L));
        routeMap.put(2,new SmsInfo("https://cloud.tencent.com",181L));
        routeMap.put(3,new SmsInfo("https://cloud.baidu.com",182L));
        return routeMap;
    }
}
