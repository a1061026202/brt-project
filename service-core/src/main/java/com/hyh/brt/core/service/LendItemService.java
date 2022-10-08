package com.hyh.brt.core.service;

import com.hyh.brt.core.pojo.entity.LendItem;
import com.hyh.brt.core.pojo.vo.InvestVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 标的出借记录表 服务类
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
public interface LendItemService extends IService<LendItem> {

    String commitInvest(InvestVo investVo);

    void notify(Map<String, Object> paramMap);

    List<LendItem> selectByLendId(Long lendId, Integer status);

    List<LendItem> selectByLendId(Long lendId);
}
