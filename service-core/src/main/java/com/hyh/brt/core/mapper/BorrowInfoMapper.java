package com.hyh.brt.core.mapper;

import com.hyh.brt.core.pojo.entity.BorrowInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 借款信息表 Mapper 接口
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
public interface BorrowInfoMapper extends BaseMapper<BorrowInfo> {

    List<BorrowInfo> selectBorrowInfoList();
}
