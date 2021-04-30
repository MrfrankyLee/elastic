package com.needayeah.elastic.controller;

import com.needayeah.elastic.common.page.Page;
import com.needayeah.elastic.common.utils.Result;
import com.needayeah.elastic.entity.XaHouse;
import com.needayeah.elastic.interfaces.HouseFace;
import com.needayeah.elastic.interfaces.request.XaHousesSearchRequest;
import com.needayeah.elastic.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author lixiaole
 * @date 2021/2/3
 */
@RestController
@RequestMapping("/api/house")
public class HouseController implements HouseFace {

    @Autowired
    private HouseService houseService;

    @Override
    public Result<String> initXaHouseForES(@RequestParam("count") Integer count) {
        return houseService.initXaHouseForES(count);
    }

    @Override
    public Result<Page<XaHouse>> searchXaHouse(@RequestBody XaHousesSearchRequest request) {
        return houseService.searchXaHouse(request);
    }
}
