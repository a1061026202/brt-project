package com.hyh.brt.core.controller.api;


import com.hyh.common.result.R;
import com.hyh.brt.base.util.JwtUtils;
import com.hyh.brt.core.pojo.vo.BorrowerVo;
import com.hyh.brt.core.service.BorrowerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 借款人 前端控制器
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
@RestController
@RequestMapping("/api/core/borrower")
@Api(tags = "借款人")
@Slf4j
public class BorrowerController {

    @Resource
    private BorrowerService borrowerService;

    @ApiOperation("保存借款人信息")
    @PostMapping("/auth/save")
    public R save(@RequestBody BorrowerVo borrowerVo, HttpServletRequest request){
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        borrowerService.saveBorrowerVoByUserId(borrowerVo,userId);

        return R.ok().message("信息提交成功");
    }

    @ApiOperation("获取借款人认证状态")
    @GetMapping("/auth/getBorrowerStatus")
    public R getBorrowerStatus(HttpServletRequest request){

        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);
        Integer status = borrowerService.getStatusByUserId(userId);
        return R.ok().data("borrowerStatus", status);
    }
}

