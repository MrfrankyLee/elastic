package com.needayeah.elastic.service.impl;


import com.needayeah.elastic.common.bloomFilter.BloomFilterHelper;
import com.needayeah.elastic.common.bloomFilter.RedisBloomFilter;
import com.needayeah.elastic.common.page.Page;
import com.needayeah.elastic.common.page.Pair;
import com.needayeah.elastic.common.threadutil.ThreadUtils;
import com.needayeah.elastic.common.utils.BeanUtils;
import com.needayeah.elastic.common.utils.HeaderThreadLocal;
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
        redisTemplate.opsForValue().set("longValue", String.valueOf(10));
        return Result.success(Page.of(searchPair.getLeft().intValue(), searchPair.getRight()));
    }

    @Override
    public Result<XaHouse> getHouseDetails(XaHousesSearchRequest request) {
     /*   Long currentThreadId = Thread.currentThread().getId();
        log.info("---------------------------" + currentThreadId + "--------------------");
        HeaderThreadLocal threadLocal = HeaderThreadLocal.getThreadInstance();
        log.info("==============token:" + threadLocal.getToken() + "===============employeeId:" + threadLocal.getEmployeeId());

        ThreadUtils.execute(() -> {
            for (int i = 0; i < 100000; i++) {

                ThreadUtils.execute(() -> {
                    System.out.println("------------------");
                });
            }
        });
        redisTemplate.opsForValue().decrement("longValue");*/
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
        redisTemplate.opsForHyperLogLog().add("HyperLogLog:0001", requestIp);
        log.info(String.valueOf(redisTemplate.opsForHyperLogLog().size("HyperLogLog:0001")));

    }

    /**
     * 因为AOF文件的更新频率通常比RDB文件的更新频率更高
     * 1. 一旦开启了AOF持久化功能 那么服务器回优先使用AOF文件来还原数据库状态
     * 2. 只有在AOF持久化功能关闭的情况下 服务器才会使用RDB来还远数据库状态
     * RDB 优点
     * 1.会生成多个数据文件,每个数据文件都带边了某一个时刻redis的数据,这种多文件的方式非常适合做冷备份
     * 2.RDB 对redis对外提供读写服务的影响非常小 可以让redis保持高性能,因为redis主进程只需要fork一个子进程 让子进程执行磁盘IO操作来进行Rdb持久化
     * 3.相对于AOF 直接基于RDB数据文件来重启和恢复redis进行 更加快速
     * RDB 缺点
     * 1.可能会丢数据,假设5分钟之前生成900条 数据 5分钟之后1200条  这个时候redis挂了  300条数据丢失了
     * <p>
     * AOF 优点
     * 1.可以更好的保护数据不丢失,一般AOF每隔一秒通过一个后台线程执行一次fsync操作,最多丢失一秒数据  保证os cache中的数据写入磁盘
     * 2.AOF日志文件以append-only模式写入，没有任何磁盘寻址的开销,写入性能非常高
     * <p>
     * 缺点
     * 1 恢复慢   文件比rdb大
     * <p>
     * 主管宕机:一个哨兵自己觉得master宕机了 就是主观宕机
     * 客观宕机: 如果quorum数量的哨兵都觉得一个master宕机了  就是客观宕机
     * <p>
     * <p>
     * 2 哨兵 slave->master选举算法
     * 如果一个master被认为客观宕机了,而且majority哨兵都允许了主备切换,那么就个选举一个slave来成为master
     * 会考虑slave的一些信息
     * (1) 跟master的断开连接时长 已经超过了某一个时间 就不适合选举成为master`
     * (2) slave优先级 优先级最高的
     * (3) 复制offset 偏移量最大的
     * (4) run id  较小的
     * <p>
     * 常用操作命令
     * <p>
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