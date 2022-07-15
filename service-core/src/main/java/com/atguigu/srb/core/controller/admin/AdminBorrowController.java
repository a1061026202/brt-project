package com.atguigu.srb.core.controller.admin;

import com.atguigu.common.result.R;
import com.atguigu.srb.core.pojo.entity.Borrower;
import com.atguigu.srb.core.pojo.vo.BorrowerApprovalVo;
import com.atguigu.srb.core.pojo.vo.BorrowerDetailVo;
import com.atguigu.srb.core.service.BorrowerService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@Slf4j
@Api(tags = "借款额度审批")
@RequestMapping("/admin/core/borrower")
public class AdminBorrowController {

    @Resource
    private BorrowerService borrowerService;

    @ApiOperation("获取借款人分页列表")
    @GetMapping("/list/{page}/{limit}")
    public R listPage(
            @ApiParam(value = "当前页码",required = true)
            @PathVariable Long page,

            @ApiParam(value = "每页记录数",required = true)
            @PathVariable Long limit,

            @ApiParam(value = "查询关键字",required = false)
            @RequestParam String keyword
    ){
        Page<Borrower> borrowerPage = new Page<>(page,limit);
        IPage<Borrower> pageModel = borrowerService.listPage(borrowerPage,keyword);
        return R.ok().data("pageModel",pageModel);
    }

    @ApiOperation("审核借款人信息")
    @GetMapping("show/{id}")
    public R showById(
            @ApiParam(value = "借款人信息",required = true)
            @PathVariable Long id){
        BorrowerDetailVo borrowerDetailVo=borrowerService.getBorrowerDetailVOById(id);

        return R.ok().data("borrowerDetailVO", borrowerDetailVo);
    }

    @ApiOperation("借款额度审批")
    @PostMapping("/approval")
    public R approval(@RequestBody BorrowerApprovalVo borrowerApprovalVo){
        borrowerService.approval(borrowerApprovalVo);
        return R.ok().message("审批完成");
    }
}
