package com.atguigu.srb.core.service;

import com.atguigu.srb.core.pojo.entity.BorrowInfo;
import com.atguigu.srb.core.pojo.entity.Lend;
import com.atguigu.srb.core.pojo.vo.BorrowInfoApprovalVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的准备表 服务类
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
public interface LendService extends IService<Lend> {

    void createLend(BorrowInfoApprovalVo borrowInfoApprovalVo, BorrowInfo borrowInfo);


    Map<String, Object> getLendDetail(Long id);

    void makeLoan(Long id);

    List<Lend> selectList(Page<Lend> lendPage);

    BigDecimal getInterestCount(BigDecimal invest, BigDecimal yearRate, Integer totalmonth, Integer returnMethod);

    List<Lend> selectList2();

}
