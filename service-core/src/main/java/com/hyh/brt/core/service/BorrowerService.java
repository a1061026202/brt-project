package com.hyh.brt.core.service;

import com.hyh.brt.core.pojo.entity.Borrower;
import com.hyh.brt.core.pojo.vo.BorrowerApprovalVo;
import com.hyh.brt.core.pojo.vo.BorrowerDetailVo;
import com.hyh.brt.core.pojo.vo.BorrowerVo;
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
