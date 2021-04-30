package com.needayeah.elastic.common.utils;

import com.google.common.collect.Lists;
import com.needayeah.elastic.common.SnowFlake;
import com.needayeah.elastic.entity.JdGoods;
import com.needayeah.elastic.entity.XaHouse;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author lixiaole
 * @date 2021-02-03
 */
@Component
@Slf4j
public class HtmlParseUtil {

    /**
     * 解析获取京东商品
     *
     * @param keyWord 关键字
     * @return List
     */
    public List<JdGoods> parseJdGoods(String keyWord) {
        List<JdGoods> jdGoodsList = Lists.newArrayList();
        try {
            String url = "https://search.jd.com/Search?keyword=" + keyWord;
            // 解析网页
            Document document = Jsoup.connect(url).userAgent("Mozilla").get();
            Element element = document.getElementById("J_goodsList");
            Elements elements = element.getElementsByTag("li");
            for (Element el : elements) {
                String id = Strings.isEmpty(el.attr("data-sku")) ? SnowFlake.strNextId() : el.attr("data-sku");
                String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
                String price = el.getElementsByClass("p-price").eq(0).text();
                String title = el.getElementsByClass("p-name").eq(0).text();
                String shopName = el.getElementsByClass("p-shop").eq(0).text();
                if (!StringUtils.hasLength(title)) {
                    continue;
                }
                jdGoodsList.add(JdGoods.builder()
                        .id(id)
                        .price(Strings.isEmpty(price) ? 0.0D : Double.valueOf(price.replace("￥", "")))
                        .goodsName(title)
                        .shopName(shopName)
                        .img(img)
                        .build());
            }
        } catch (Exception e) {
            log.error("parseJdGoods fail", e);
        }
        return jdGoodsList;
    }

    public List<XaHouse> parseHouse(int count) {
        String url = "https://xian.esf.fang.com/house/g23-i3";
        List<XaHouse> xaHouseList = Lists.newArrayList();
        // 解析网页
        for (int i = 1; i < count; i++) {
            try {
                String httpUrl = url + i + "?rfss=2-ac28bc872bb84d8b11-13#";
                Document document = Jsoup.connect(httpUrl).userAgent("Mozilla").get();
                Element element = document.getElementsByClass("shop_list").get(0);
                Elements elementsByTag = element.getElementsByTag("dl");
                for (Element element1 : elementsByTag) {
                    Elements elements = element1.getElementsByClass("tit_shop");
                    if (elements.size() == 0) {
                        continue;
                    }
                    String title = Objects.isNull(elements) ? Strings.EMPTY : elements.get(0).text();
                    String info = Objects.isNull(element1.getElementsByClass("tel_shop")) ? Strings.EMPTY : element1.getElementsByClass("tel_shop").get(0).text();
                    String[] array = info.replace(" | ", ",").split(",");
                    String unitType = array[0];
                    Double area = Double.valueOf(array[1].replace("㎡", ""));
                    String storeyHeight = array[2];
                    String towards = array[3];
                    Integer buildYear = !array[4].contains("年建") ? 0 : Integer.valueOf(array[4].replace("年建", ""));
                    String address = Objects.isNull(element1.getElementsByClass("add_shop")) ? Strings.EMPTY : element1.getElementsByClass("add_shop").get(0).text();
                    Elements price_right = element1.getElementsByClass("price_right").get(0).getElementsByTag("span");
                    Double totalPrice = Double.valueOf(price_right.get(0).text().replace("万", ""));
                    Double unitPrice = Double.valueOf(price_right.get(1).text().replace("元/㎡", ""));
                    xaHouseList.add(XaHouse.builder()
                            .id(SnowFlake.strNextId())
                            .title(title)
                            .unitType(unitType)
                            .area(area)
                            .storeyHeight(storeyHeight)
                            .towards(towards)
                            .address(address)
                            .buildYear(buildYear)
                            .totalPrice(totalPrice)
                            .unitPrice(unitPrice)
                            .build());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return xaHouseList;
    }
}
