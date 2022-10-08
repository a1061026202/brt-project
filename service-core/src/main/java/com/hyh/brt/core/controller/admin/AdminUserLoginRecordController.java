package com.hyh.brt.core.controller.admin;


import com.hyh.common.result.R;
import com.hyh.brt.core.pojo.entity.UserLoginRecord;
import com.hyh.brt.core.service.UserLoginRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户登录记录表 前端控制器
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
@RestController
@RequestMapping("/admin/core/userLoginRecord")
//@CrossOrigin
@Api(tags = "登陆日志")
public class AdminUserLoginRecordController {


    @Resource
    private UserLoginRecordService userLoginRecordService;

    @ApiOperation("获取会员登录日志列表")
    @GetMapping("/listTop50/{userId}")
    public R listTop50(
            @ApiParam(value ="用户id", required = true)
            @PathVariable Long userId){

        List<UserLoginRecord> userLoginRecordList = userLoginRecordService.listTop50(userId);
        return R.ok().data("list", userLoginRecordList);
    }

}

