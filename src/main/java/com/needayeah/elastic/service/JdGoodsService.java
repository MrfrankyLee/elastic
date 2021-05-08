package com.needayeah.elastic.service;


import com.needayeah.elastic.common.utils.Result;
import com.needayeah.elastic.common.page.Page;
import com.needayeah.elastic.interfaces.reponse.JdGoodsResponse;
import com.needayeah.elastic.interfaces.request.JdGoodsSearchRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author lixiaole
 * @date 2021/2/3
 */

public interface JdGoodsService {

    Result<String> initJDGoodsForES(String keyWord);

    Result<Page<JdGoodsResponse>> searchJdGoods(JdGoodsSearchRequest jdGoodsSearchRequest);

    Result<String> uploadPic(MultipartFile file);
}
