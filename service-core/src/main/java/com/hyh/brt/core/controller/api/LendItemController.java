package com.hyh.brt.core.controller.api;


import com.alibaba.fastjson.JSON;
import com.hyh.common.result.R;
import com.hyh.brt.base.util.JwtUtils;
import com.hyh.brt.core.hfb.RequestHelper;
import com.hyh.brt.core.pojo.entity.LendItem;
import com.hyh.brt.core.pojo.vo.InvestVo;
import com.hyh.brt.core.service.LendItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 前端控制器
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
@Api(tags = "标的的投资")
@RestController
@RequestMapping("/api/core/lendItem")
@Slf4j
public class LendItemController {

    @Resource
    LendItemService lendItemService;

    @ApiOperation("会员投资提交数据")
    @PostMapping("/auth/commitInvest")
    public R commitInvest(@RequestBody InvestVo investVo, HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        String userName = JwtUtils.getUserName(token);
        investVo.setInvestUserId(userId);
        investVo.setInvestName(userName);
        log.info(investVo.toString());

        //构建充值自动提交表单
        String formStr = lendItemService.commitInvest(investVo);
        return R.ok().data("formStr",formStr);
    }

    @ApiOperation("会员投资异步回调")
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());

        log.info("用户投资异步回调"+ JSON.toJSONString(paramMap));

        //校验签名
        if(RequestHelper.isSignEquals(paramMap)){
            if("0001".equals(paramMap.get("resultCode"))){
                lendItemService.notify(paramMap);
            }else {
                log.info("用户投资异步回调失败"+JSON.toJSONString(paramMap));
                return "fail";
            }
        }else {
            log.info("用户投资异步回调签名错误：" + JSON.toJSONString(paramMap));
            return "fail";
        }
        return "success";

    }

    @ApiOperation("获取列表")
    @GetMapping("/list/{lendId}")
    public R list(
            @ApiParam(value = "标的id", required = true)
            @PathVariable Long lendId) {
        List<LendItem> list = lendItemService.selectByLendId(lendId);
        return R.ok().data("list", list);
    }


}

