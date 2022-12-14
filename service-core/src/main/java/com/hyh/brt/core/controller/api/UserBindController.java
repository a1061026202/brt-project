package com.hyh.brt.core.controller.api;


import com.alibaba.fastjson.JSON;
import com.hyh.common.result.R;
import com.hyh.brt.base.util.JwtUtils;
import com.hyh.brt.core.hfb.RequestHelper;
import com.hyh.brt.core.pojo.vo.UserBindVo;
import com.hyh.brt.core.service.UserBindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 前端控制器
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
@Api(tags = "会员账号绑定")
@Slf4j
@RestController
@RequestMapping("/api/core/userBind")
public class UserBindController {

    @Resource
    private UserBindService userBindService;

    @ApiOperation("账户绑定提交数据")
    @PostMapping("/auth/bind")
    public R bind(@RequestBody UserBindVo userBindVo, HttpServletRequest request){

        // 判断请求头是否包含token,并从token中解析出user_id
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        //根据userId做账户绑定并生成动态表单的字符串
        String formStr = userBindService.commitBindUser(userBindVo,userId);

        return R.ok().data("formStr",formStr);
    }

    @ApiOperation("账户绑定异步回调")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request){

        //汇付宝向尚融宝发起回调请求时携带的参数
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        log.info("账户绑定异步回调接收的参数如下：" + JSON.toJSONString(paramMap));

        //验签
        if(!RequestHelper.isSignEquals(paramMap)){
            log.error("用户账号绑定异步回调签名验证错误：" + JSON.toJSONString(paramMap));
            return "fail";
        }

        log.info("验签成功！开始账户绑定");
        userBindService.notify(paramMap);

        return "success";
    }

}

