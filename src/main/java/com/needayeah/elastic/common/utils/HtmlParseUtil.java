package com.needayeah.elastic.common.utils;

import com.google.common.collect.Lists;
import com.needayeah.elastic.entity.JdGoods;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URL;
import java.util.List;

/**
 * @author lixiaole
 * @date 2021-02-03
 */
@Component
@Slf4j
public class HtmlParseUtil {

    /**
     * 解析获取京东商品
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
                String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");;
                String price = el.getElementsByClass("p-price").eq(0).text();
                String title = el.getElementsByClass("p-name").eq(0).text();
                String shopName = el.getElementsByClass("p-shop").eq(0).text();
                 if (!StringUtils.hasLength(title)) {
                     continue;
                 }
                jdGoodsList.add(JdGoods.builder()
                        .price(price)
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
}
