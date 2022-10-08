package com.hyh.brt.sms.controller;


import com.hyh.common.exception.Assert;
import com.hyh.common.result.R;
import com.hyh.common.result.ResponseEnum;
import com.hyh.common.util.RandomUtils;
import com.hyh.common.util.RegexValidateUtils;
import com.hyh.brt.sms.client.CoreUserInfoClient;
import com.hyh.brt.sms.service.sendSms;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/sms")
@Api(tags = "短信管理")
//@CrossOrigin
@Slf4j
public class ApiSmsController {

    @Resource
    private sendSms sendSms;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private CoreUserInfoClient coreUserInfoClient;

    @GetMapping("/send/{mobile}")
    @ApiOperation("获取验证码")
    public R send(
            @ApiParam(value = "手机号码",required = true)
            @PathVariable String mobile){

        // 判断手机号是否为空
        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        // 判断手机号是否合法
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile),ResponseEnum.MOBILE_ERROR);

        //判断手机号是否已注册
        Assert.isTrue(!coreUserInfoClient.checkMobile(mobile),ResponseEnum.MOBILE_EXIST_ERROR);


        String code = RandomUtils.getFourBitRandom();
        log.info("验证码是: {}",code);
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);

        // sendSms.send(mobile,map);

        redisTemplate.opsForValue().set("srb:sms:code:"+mobile,code,5, TimeUnit.MINUTES);

        return R.ok().message("获取验证码成功");
    }
}
