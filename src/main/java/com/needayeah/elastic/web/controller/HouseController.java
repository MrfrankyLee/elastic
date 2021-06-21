package com.needayeah.elastic.web.controller;

import com.needayeah.elastic.common.annotation.DataPrivilegeInjection;
import com.needayeah.elastic.common.annotation.PrivilegeFieldEnum;
import com.needayeah.elastic.common.page.Page;
import com.needayeah.elastic.common.utils.Result;
import com.needayeah.elastic.entity.XaHouse;
import com.needayeah.elastic.interfaces.HouseFace;
import com.needayeah.elastic.interfaces.request.XaHousesSearchRequest;
import com.needayeah.elastic.service.HouseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(tags = "西安二手房(三室)")
public class HouseController implements HouseFace {

    @Autowired
    private HouseService houseService;

    @Override
    @ApiOperation("初始化房屋到ES")
    public Result<String> initXaHouseForES(@ApiParam(value = "页数(每页100条)" ,name = "count") @RequestParam("count") Integer count) {
        return houseService.initXaHouseForES(count);
    }

    @Override
    @ApiOperation("搜索房屋")
    public Result<Page<XaHouse>> searchXaHouse(@RequestBody XaHousesSearchRequest request) {
        return houseService.searchXaHouse(request);
    }

    @Override
    @DataPrivilegeInjection(fields = {PrivilegeFieldEnum.keyWord})
    public Result<XaHouse> getHouseDetails(@RequestBody XaHousesSearchRequest request) {
        return houseService.getHouseDetails(request);
    }
}
