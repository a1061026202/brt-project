package com.atguigu.srb.core.controller.admin;


import com.atguigu.common.exception.Assert;
import com.atguigu.common.result.R;
import com.atguigu.common.result.ResponseEnum;
import com.atguigu.srb.core.pojo.entity.IntegralGrade;
import com.atguigu.srb.core.service.IntegralGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 积分等级表 前端控制器
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
//@CrossOrigin
@RestController
@RequestMapping("/admin/core/integralGrade")
@Api(tags = "积分等级管理")
@Slf4j
public class AdminIntegralGradeController {

    @Resource
    private IntegralGradeService integralGradeService;

    @GetMapping("/list")
    @ApiOperation("积分等级列表")
    public R ListAll(){
        return R.ok().data("list",integralGradeService.list());
    }

    @DeleteMapping("/remove/{id}")
    @ApiOperation(value = "根据id删除积分等级",notes = "逻辑删除数据记录")
    public R RemoveById(
            @ApiParam(value = "数据id",example = "32")
            @PathVariable Long id){
        boolean b = integralGradeService.removeById(id);
        if (b){
            return R.ok().code(200).message("删除成功");
        }
        return R.error();
    }


    @PostMapping("/save")
    @ApiOperation(value = "新增积分")
    public R save(
            @ApiParam(value = "新增积分数据")
            @RequestBody IntegralGrade integralGrade){

        Assert.notNull(integralGrade.getBorrowAmount(), ResponseEnum.BORROW_AMOUNT_NULL_ERROR);
        log.info("新增数据为 ===> {}",integralGrade);
        boolean save = integralGradeService.save(integralGrade);
        if (save){
            return R.ok().code(200).message("新增成功");
        }else {
            return R.error().message("新增失败");
        }
    }

    @ApiOperation(value = "通过id查询积分数据")
    @GetMapping("/get/{id}")
    public R getById(
            @ApiParam(value = "数据id",required = true)
            @PathVariable Long id){
        IntegralGrade byId = integralGradeService.getById(id);
        if(byId!=null){
            return R.ok().data("record",byId);
        }else {
            return R.error().message("数据为空");
        }

    }

    @PutMapping("/update")
    @ApiOperation(value = "修改积分")
    public R updateById(
            @ApiParam(value = "修改积分数据",required = true)
            @RequestBody IntegralGrade integralGrade){
        // log.info("数据为: {}",integralGrade);
        if(integralGrade.getIntegralStart() >= integralGrade.getIntegralEnd()){
            return R.error().message("参数不合法");
        }
        boolean update = integralGradeService.updateById(integralGrade);
        if (update){
            return R.ok().code(200).message("修改成功");
        }else {
            return R.error().message("修改失败");
        }
    }


}

