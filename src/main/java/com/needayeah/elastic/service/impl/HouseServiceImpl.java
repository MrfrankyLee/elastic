package com.needayeah.elastic.service.impl;


import cn.hutool.core.lang.tree.Node;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
      */
        redisTemplate.opsForValue().decrement("longValue");
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


    public static void main(String[] args) {
        /*HashMap<String, Integer> map = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            map.put(String.valueOf(i) + "key", i);
            if (i == 10) {
                for (int j = 10; j >= 0; j--) {
                    map.put(String.valueOf(j) + "key", j);
                }
            }
        }
        System.out.println(map.toString());
        */
        int n = 16;
        int sc = n - (n >>> 2);
        System.out.println(sc);
    }

    public static int tableSizeFor(int cap) {
        int MAXIMUM_CAPACITY = 1 << 30;
        //经过下面的 或 和位移 运算， n最终各位都是1。
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        //判断n是否越界，返回 2的n次方作为 table（哈希桶）的阈值
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }














    /**
     * Moves and/or copies the nodes in each bin to new table. See
     * above for explanation.
     *
     * transferIndex 表示转移时的下标，初始为扩容前的 length。
     *
     * 我们假设长度是 32
     */
   /* private final void transfer(Node<K,V>[] tab, Node<K,V>[] nextTab) {
        int n = tab.length, stride;
        // 将 length / 8 然后除以 CPU核心数。如果得到的结果小于 16，那么就使用 16。
        // 这里的目的是让每个 CPU 处理的桶一样多，避免出现转移任务不均匀的现象，如果桶较少的话，默认一个 CPU（一个线程）处理 16 个桶
        if ((stride = (NCPU > 1) ? (n >>> 3) / NCPU : n) < MIN_TRANSFER_STRIDE)
            stride = MIN_TRANSFER_STRIDE; // subdivide range 细分范围 stridea：TODO
        // 新的 table 尚未初始化
        if (nextTab == null) {            // initiating
            try {
                // 扩容  2 倍
                Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n << 1];
                // 更新
                nextTab = nt;
            } catch (Throwable ex) {      // try to cope with OOME
                // 扩容失败， sizeCtl 使用 int 最大值。
                sizeCtl = Integer.MAX_VALUE;
                return;// 结束
            }
            // 更新成员变量
            nextTable = nextTab;
            // 更新转移下标，就是 老的 tab 的 length
            transferIndex = n;
        }
        // 新 tab 的 length
        int nextn = nextTab.length;
        // 创建一个 fwd 节点，用于占位。当别的线程发现这个槽位中是 fwd 类型的节点，则跳过这个节点。
        ForwardingNode<K,V> fwd = new ForwardingNode<K,V>(nextTab);
        // 首次推进为 true，如果等于 true，说明需要再次推进一个下标（i--），反之，如果是 false，那么就不能推进下标，需要将当前的下标处理完毕才能继续推进
        boolean advance = true;
        // 完成状态，如果是 true，就结束此方法。
        boolean finishing = false; // to ensure sweep before committing nextTab
        // 死循环,i 表示下标，bound 表示当前线程可以处理的当前桶区间最小下标
        for (int i = 0, bound = 0;;) {
            Node<K,V> f; int fh;
            // 如果当前线程可以向后推进；这个循环就是控制 i 递减。同时，每个线程都会进入这里取得自己需要转移的桶的区间
            while (advance) {
                int nextIndex, nextBound;
                // 对 i 减一，判断是否大于等于 bound （正常情况下，如果大于 bound 不成立，说明该线程上次领取的任务已经完成了。那么，需要在下面继续领取任务）
                // 如果对 i 减一大于等于 bound（还需要继续做任务），或者完成了，修改推进状态为 false，不能推进了。任务成功后修改推进状态为 true。
                // 通常，第一次进入循环，i-- 这个判断会无法通过，从而走下面的 nextIndex 赋值操作（获取最新的转移下标）。其余情况都是：如果可以推进，将 i 减一，然后修改成不可推进。如果 i 对应的桶处理成功了，改成可以推进。
                if (--i >= bound || finishing)
                    advance = false;// 这里设置 false，是为了防止在没有成功处理一个桶的情况下却进行了推进
                    // 这里的目的是：1. 当一个线程进入时，会选取最新的转移下标。2. 当一个线程处理完自己的区间时，如果还有剩余区间的没有别的线程处理。再次获取区间。
                else if ((nextIndex = transferIndex) <= 0) {
                    // 如果小于等于0，说明没有区间了 ，i 改成 -1，推进状态变成 false，不再推进，表示，扩容结束了，当前线程可以退出了
                    // 这个 -1 会在下面的 if 块里判断，从而进入完成状态判断
                    i = -1;
                    advance = false;// 这里设置 false，是为了防止在没有成功处理一个桶的情况下却进行了推进
                }// CAS 修改 transferIndex，即 length - 区间值，留下剩余的区间值供后面的线程使用
                else if (U.compareAndSwapInt
                        (this, TRANSFERINDEX, nextIndex,
                                nextBound = (nextIndex > stride ?
                                        nextIndex - stride : 0))) {
                    bound = nextBound;// 这个值就是当前线程可以处理的最小当前区间最小下标
                    i = nextIndex - 1; // 初次对i 赋值，这个就是当前线程可以处理的当前区间的最大下标
                    advance = false; // 这里设置 false，是为了防止在没有成功处理一个桶的情况下却进行了推进，这样对导致漏掉某个桶。下面的 if (tabAt(tab, i) == f) 判断会出现这样的情况。
                }
            }// 如果 i 小于0 （不在 tab 下标内，按照上面的判断，领取最后一段区间的线程扩容结束）
            //  如果 i >= tab.length(不知道为什么这么判断)
            //  如果 i + tab.length >= nextTable.length  （不知道为什么这么判断）
            if (i < 0 || i >= n || i + n >= nextn) {
                int sc;
                if (finishing) { // 如果完成了扩容
                    nextTable = null;// 删除成员变量
                    table = nextTab;// 更新 table
                    sizeCtl = (n << 1) - (n >>> 1); // 更新阈值
                    return;// 结束方法。
                }// 如果没完成
                if (U.compareAndSwapInt(this, SIZECTL, sc = sizeCtl, sc - 1)) {// 尝试将 sc -1. 表示这个线程结束帮助扩容了，将 sc 的低 16 位减一。
                    if ((sc - 2) != resizeStamp(n) << RESIZE_STAMP_SHIFT)// 如果 sc - 2 不等于标识符左移 16 位。如果他们相等了，说明没有线程在帮助他们扩容了。也就是说，扩容结束了。
                        return;// 不相等，说明没结束，当前线程结束方法。
                    finishing = advance = true;// 如果相等，扩容结束了，更新 finising 变量
                    i = n; // 再次循环检查一下整张表
                }
            }
            else if ((f = tabAt(tab, i)) == null) // 获取老 tab i 下标位置的变量，如果是 null，就使用 fwd 占位。
                advance = casTabAt(tab, i, null, fwd);// 如果成功写入 fwd 占位，再次推进一个下标
            else if ((fh = f.hash) == MOVED)// 如果不是 null 且 hash 值是 MOVED。
                advance = true; // already processed // 说明别的线程已经处理过了，再次推进一个下标
            else {// 到这里，说明这个位置有实际值了，且不是占位符。对这个节点上锁。为什么上锁，防止 putVal 的时候向链表插入数据
                synchronized (f) {
                    // 判断 i 下标处的桶节点是否和 f 相同
                    if (tabAt(tab, i) == f) {
                        Node<K,V> ln, hn;// low, height 高位桶，低位桶
                        // 如果 f 的 hash 值大于 0 。TreeBin 的 hash 是 -2
                        if (fh >= 0) {
                            // 对老长度进行与运算（第一个操作数的的第n位于第二个操作数的第n位如果都是1，那么结果的第n为也为1，否则为0）
                            // 由于 Map 的长度都是 2 的次方（000001000 这类的数字），那么取于 length 只有 2 种结果，一种是 0，一种是1
                            //  如果是结果是0 ，Doug Lea 将其放在低位，反之放在高位，目的是将链表重新 hash，放到对应的位置上，让新的取于算法能够击中他。
                            int runBit = fh & n;
                            Node<K,V> lastRun = f; // 尾节点，且和头节点的 hash 值取于不相等
                            // 遍历这个桶
                            for (Node<K,V> p = f.next; p != null; p = p.next) {
                                // 取于桶中每个节点的 hash 值
                                int b = p.hash & n;
                                // 如果节点的 hash 值和首节点的 hash 值取于结果不同
                                if (b != runBit) {
                                    runBit = b; // 更新 runBit，用于下面判断 lastRun 该赋值给 ln 还是 hn。
                                    lastRun = p; // 这个 lastRun 保证后面的节点与自己的取于值相同，避免后面没有必要的循环
                                }
                            }
                            if (runBit == 0) {// 如果最后更新的 runBit 是 0 ，设置低位节点
                                ln = lastRun;
                                hn = null;
                            }
                            else {
                                hn = lastRun; // 如果最后更新的 runBit 是 1， 设置高位节点
                                ln = null;
                            }// 再次循环，生成两个链表，lastRun 作为停止条件，这样就是避免无谓的循环（lastRun 后面都是相同的取于结果）
                            for (Node<K,V> p = f; p != lastRun; p = p.next) {
                                int ph = p.hash; K pk = p.key; V pv = p.val;
                                // 如果与运算结果是 0，那么就还在低位
                                if ((ph & n) == 0) // 如果是0 ，那么创建低位节点
                                    ln = new Node<K,V>(ph, pk, pv, ln);
                                else // 1 则创建高位
                                    hn = new Node<K,V>(ph, pk, pv, hn);
                            }
                            // 其实这里类似 hashMap
                            // 设置低位链表放在新链表的 i
                            setTabAt(nextTab, i, ln);
                            // 设置高位链表，在原有长度上加 n
                            setTabAt(nextTab, i + n, hn);
                            // 将旧的链表设置成占位符
                            setTabAt(tab, i, fwd);
                            // 继续向后推进
                            advance = true;
                        }// 如果是红黑树
                        else if (f instanceof TreeBin) {
                            TreeBin<K,V> t = (TreeBin<K,V>)f;
                            TreeNode<K,V> lo = null, loTail = null;
                            TreeNode<K,V> hi = null, hiTail = null;
                            int lc = 0, hc = 0;
                            // 遍历
                            for (Node<K,V> e = t.first; e != null; e = e.next) {
                                int h = e.hash;
                                TreeNode<K,V> p = new TreeNode<K,V>
                                        (h, e.key, e.val, null, null);
                                // 和链表相同的判断，与运算 == 0 的放在低位
                                if ((h & n) == 0) {
                                    if ((p.prev = loTail) == null)
                                        lo = p;
                                    else
                                        loTail.next = p;
                                    loTail = p;
                                    ++lc;
                                } // 不是 0 的放在高位
                                else {
                                    if ((p.prev = hiTail) == null)
                                        hi = p;
                                    else
                                        hiTail.next = p;
                                    hiTail = p;
                                    ++hc;
                                }
                            }
                            // 如果树的节点数小于等于 6，那么转成链表，反之，创建一个新的树
                            ln = (lc <= UNTREEIFY_THRESHOLD) ? untreeify(lo) :
                                    (hc != 0) ? new TreeBin<K,V>(lo) : t;
                            hn = (hc <= UNTREEIFY_THRESHOLD) ? untreeify(hi) :
                                    (lc != 0) ? new TreeBin<K,V>(hi) : t;
                            // 低位树
                            setTabAt(nextTab, i, ln);
                            // 高位数
                            setTabAt(nextTab, i + n, hn);
                            // 旧的设置成占位符
                            setTabAt(tab, i, fwd);
                            // 继续向后推进
                            advance = true;
                        }
                    }
                }
            }
        }
    }*/
}