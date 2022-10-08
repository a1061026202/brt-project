package com.hyh.brt.core.controller.api;


import com.hyh.common.exception.Assert;
import com.hyh.common.result.R;
import com.hyh.common.result.ResponseEnum;
import com.hyh.common.util.RegexValidateUtils;
import com.hyh.brt.base.util.JwtUtils;
import com.hyh.brt.core.pojo.vo.LoginVo;
import com.hyh.brt.core.pojo.vo.RegisterVo;
import com.hyh.brt.core.pojo.vo.UserIndexVo;
import com.hyh.brt.core.pojo.vo.UserInfoVo;
import com.hyh.brt.core.service.UserInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 用户基本信息 前端控制器
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
@RestController
//@CrossOrigin
@Api(tags = "会员接口")
@Slf4j
@RequestMapping("/api/core/userInfo")
public class UserInfoController {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserInfoService userInfoService;


    @PostMapping("/register")
    @ApiOperation("会员注册")
    public R register(
            @ApiParam(value = "用户对象",required = true)
            @RequestBody RegisterVo registerVo){

        String mobile = registerVo.getMobile();
        String code = registerVo.getCode();
        String password = registerVo.getPassword();


        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        Assert.notEmpty(password, ResponseEnum.PASSWORD_NULL_ERROR);
        Assert.notEmpty(code, ResponseEnum.CODE_NULL_ERROR);
        Assert.isTrue(RegexValidateUtils.checkCellphone(mobile), ResponseEnum.MOBILE_ERROR);

        String codeGen = (String) redisTemplate.opsForValue().get("srb:sms:code:" + mobile);
        Assert.equals(code, codeGen, ResponseEnum.CODE_ERROR);

        //注册
        userInfoService.register(registerVo);

        return R.ok().message("注册成功");
    }

    @ApiOperation("会员登陆")
    @PostMapping("/login")
    public R login(
            @ApiParam(value = "登陆对象",required = true)
            @RequestBody LoginVo loginVo,
            HttpServletRequest httpServletRequest){
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();

        Assert.notEmpty(mobile, ResponseEnum.MOBILE_NULL_ERROR);
        Assert.notEmpty(password, ResponseEnum.PASSWORD_NULL_ERROR);

        String ip = httpServletRequest.getRemoteAddr();

        UserInfoVo userInfoVo = userInfoService.login(loginVo, ip);

        return R.ok().data("userInfo", userInfoVo);
    }

    @ApiOperation("校验令牌")
    @GetMapping("/checkToken")
    public R checkToken(HttpServletRequest request) {

        String token = request.getHeader("token");
        boolean result = JwtUtils.checkToken(token);

        if(result){
            return R.ok();
        }else{
            return R.setResult(ResponseEnum.LOGIN_AUTH_ERROR);
        }

    }

    @ApiOperation("验证手机号码是否存在")
    @GetMapping("/checkMobile/{mobile}")
    public boolean checkMobile(@PathVariable String mobile){
        return userInfoService.checkMobile(mobile);
    }

    @ApiOperation("获取个人空间用户信息")
    @GetMapping("/auth/getIndexUserInfo")
    public R getIndexUserInfo(HttpServletRequest request) {
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        UserIndexVo userIndexVo = userInfoService.getIndexUserInfo(userId);
        return R.ok().data("userIndexVO", userIndexVo);
    }

}

