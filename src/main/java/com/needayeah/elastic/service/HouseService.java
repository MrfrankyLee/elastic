package com.needayeah.elastic.service;


import com.needayeah.elastic.common.utils.Result;
import com.needayeah.elastic.common.page.Page;
import com.needayeah.elastic.entity.XaHouse;
import com.needayeah.elastic.interfaces.request.XaHousesSearchRequest;

/**
 * @author lixiaole
 * @date 2021/2/3
 */

public interface HouseService {


    Result<String> initXaHouseForES(Integer count);

    Result<Page<XaHouse>> searchXaHouse(XaHousesSearchRequest request);

    Result<XaHouse> getHouseDetails(XaHousesSearchRequest request);
}
