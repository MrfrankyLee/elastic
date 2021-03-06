package com.needayeah.elastic.interfaces;

import com.needayeah.elastic.common.page.Page;
import com.needayeah.elastic.common.utils.Result;
import com.needayeah.elastic.interfaces.reponse.JdGoodsResponse;
import com.needayeah.elastic.interfaces.request.InitJdGoodsRequest;
import com.needayeah.elastic.interfaces.request.JdGoodsSearchRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author lixiaole
 * @date 2021/2/3
 */

public interface JdGoodsFace {

    /**
     * 京东商品初始化加载到ES
     *
     * @return
     */
    @PostMapping("/initJDGoodsForES")
    Result<String> initJDGoodsForES(@RequestBody InitJdGoodsRequest request);

    /**
     * 根据关键字搜索商品
     *
     * @return
     */
    @PostMapping("/searchJdGoods")
    Result<Page<JdGoodsResponse>> searchJdGoods(@RequestBody JdGoodsSearchRequest request);

    /**
     * 上传图片
     *
     * @return
     */
    @PostMapping("/uploadPic")
    Result<String> uploadPic(@RequestParam("file") MultipartFile file);


}
