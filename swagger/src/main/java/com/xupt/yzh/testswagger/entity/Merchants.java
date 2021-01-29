package com.xupt.yzh.testswagger.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Merchants {


    /** 商户 id, 主键 */
    private Integer cid;

    /** 商户名称, 需要是全局唯一的 */
    private String name;

    /** 商户 logo */
    private String logoUrl;

    /** 商户营业执照 */
    private String businessLicenseUrl;

    /** 商户的联系电话 */
    private String phone;

    /** 商户地址 */
    private String address;

    /** 商户是否通过审核 */
    private Boolean isAudit = false;



}
