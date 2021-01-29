package com.xupt.yzh.testswagger.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("创建商户的请求对象")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateMerchantsRequest {

    @ApiModelProperty("商户名")
    private String name;

    @ApiModelProperty("商户logo的URL")
    private String logoUrl;

    @ApiModelProperty("营业执照URL")
    private String businessLicenseUrl;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("地址")
    private String address;

}
