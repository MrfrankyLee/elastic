package com.needayeah.elastic.service.impl;


import com.needayeah.elastic.common.bloomFilter.BloomFilterHelper;
import com.needayeah.elastic.common.bloomFilter.RedisBloomFilter;
import com.needayeah.elastic.common.page.Page;
import com.needayeah.elastic.common.page.Pair;
import com.needayeah.elastic.common.utils.BeanUtils;
import com.needayeah.elastic.common.utils.HtmlParseUtil;
import com.needayeah.elastic.common.utils.NumberUtils;
import com.needayeah.elastic.common.utils.Result;
import com.needayeah.elastic.domain.XaHouseEsDomain;
import com.needayeah.elastic.entity.XaHouse;
import com.needayeah.elastic.interfaces.request.XaHousesSearchRequest;
import com.needayeah.elastic.model.XaHouseSearchBO;
import com.needayeah.elastic.service.HouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author lixiaole
 * @date 2021/2/3
 */
@Slf4j
@Service
public class HouseServiceImpl implements HouseService {

    private static final int SEARCH_TRADE_ORDER_MAX_NUM = 10000;

    @Autowired
    private XaHouseEsDomain xaHouseEsDomain;

    @Autowired
    private HtmlParseUtil htmlParseUtil;

    @Autowired
    private RedisBloomFilter redisBloomFilter;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static BloomFilterHelper<String> bloomFilterHelper = new BloomFilterHelper<>(100000);

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
        redisBloomFilter.addList(bloomFilterHelper, "HouseFilter", searchPair.getRight().stream().map(x -> x.getId()).collect(Collectors.toList()));
        return Result.success(Page.of(searchPair.getLeft().intValue(), searchPair.getRight()));
    }

    @Override
    public Result<XaHouse> getHouseDetails(XaHousesSearchRequest request) {
        boolean flag = redisBloomFilter.contains(bloomFilterHelper, "HouseFilter", request.getId());
        if (!flag) {
            return null;
        }

        Pair<Long, List<XaHouse>> searchPair = xaHouseEsDomain.search(BeanUtils.reqTransform(XaHouseSearchBO.class, request), true);
        if (searchPair.getLeft() < 1) {
            return null;
        }
        return Result.success(searchPair.getRight().get(0));
    }

    /**
     * 计算你与商铺之间的距离
     */
    public void addLocation() {
        redisTemplate.opsForGeo().add("nearByShops", new Point(116.49428833935545, 39.86700462665782), "张三");

        redisTemplate.opsForGeo().add("nearByShops", new Point(116.45961274121092, 39.87517301328063), "YY小吃店");

        Distance distance = redisTemplate.opsForGeo().distance("nearByShops", "张三", "YY小吃店");
        System.out.println("距离是：" + distance);

        // 查找身边的店铺
        GeoResults<RedisGeoCommands.GeoLocation<Object>> results = redisTemplate.opsForGeo().radius("nearByShops", "张三", new Distance(5000));
        if (Objects.nonNull(results)) {
            results.getContent().forEach(x -> {
                System.out.println("附件得小吃店:" + x.getContent().getName());
            });
        }
    }

    /**
     * 统计页面访问uv
     */
    public void totalVisit(String requestIp) {
        redisTemplate.opsForHyperLogLog().add("HyperLogLog:0001", requestIp.length());
        log.info(String.valueOf(redisTemplate.opsForHyperLogLog().size("HyperLogLog:0001")));

    }

    /**
     * 常用操作命令
     */
    public void option() {

        // 生成给定长度的编码 例如XF000001
        NumberUtils.formatLength("XF", redisTemplate.opsForValue().increment("key"), 6);

        // setNx
        redisTemplate.opsForValue().setIfAbsent("key", "value");

        // strLen
        redisTemplate.opsForValue().size("key");
        // getRange
        redisTemplate.opsForValue().get("key", 0, 100);
        // append
        redisTemplate.opsForValue().append("key", "value");
    }
}