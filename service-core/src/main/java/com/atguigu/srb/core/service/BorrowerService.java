package com.atguigu.srb.core.service;

import com.atguigu.srb.core.pojo.entity.Borrower;
import com.atguigu.srb.core.pojo.vo.BorrowerApprovalVo;
import com.atguigu.srb.core.pojo.vo.BorrowerDetailVo;
import com.atguigu.srb.core.pojo.vo.BorrowerVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 借款人 服务类
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
public interface BorrowerService extends IService<Borrower> {

    void saveBorrowerVoByUserId(BorrowerVo borrowerVo, Long userId);

    Integer getStatusByUserId(Long userId);

    IPage<Borrower> listPage(Page<Borrower> borrowerPage, String keyword);

    BorrowerDetailVo getBorrowerDetailVOById(Long id);

    void approval(BorrowerApprovalVo borrowerApprovalVo);
}
