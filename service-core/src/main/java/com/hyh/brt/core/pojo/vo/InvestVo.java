package com.hyh.brt.core.pojo.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(description = "投标信息")
public class InvestVo {

    private Long lendId;

    //投标金额
    private String investAmount;

    //用户id
    private Long investUserId;

    //用户姓名
    private String investName;
}
