package com.xupt.yzh.testswagger.controller;

import com.xupt.yzh.testswagger.entity.Merchants;
import com.xupt.yzh.testswagger.vo.CreateMerchantsRequest;
import com.xupt.yzh.testswagger.vo.Response;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

@Api(tags = "商户相关接口")
@RestController
@RequestMapping("/merchants")
public class MerchantsController {


    @ApiOperation(value = "创建商户", notes = "开发中")
    @ApiResponses({
            @ApiResponse(code = 1, message = "非HTTP状态码，Response code字段值，描述：成功，返回该商户ID"),
            @ApiResponse(code = 0, message = "非HTTP状态码，Response code字段值，描述：失败")
    })
    @PostMapping("/create")
    public Response createMerchants(
            @ApiParam(name = "request", value = "创建商户请求对象", required = true) @RequestBody CreateMerchantsRequest request) {
        return null;
    }


    @ApiOperation("获取商户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "商户ID")
    })
    @ApiResponses({
            @ApiResponse(code = 1, message = "非HTTP状态码，Response code字段值，描述：成功", response = Merchants.class),
            @ApiResponse(code = 0, message = "非HTTP状态码，Response code字段值，描述：失败")
    })
    @GetMapping("/{id}")
    public Response getMerchantsInfo(@PathVariable Integer id) {
        return null;
    }

}
