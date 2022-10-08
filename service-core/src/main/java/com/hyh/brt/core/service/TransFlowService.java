package com.hyh.brt.core.service;

import com.hyh.brt.core.pojo.bo.TransFlowBo;
import com.hyh.brt.core.pojo.entity.TransFlow;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 交易流水表 服务类
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
public interface TransFlowService extends IService<TransFlow> {

    boolean isSaveTranFlow(String agentBillNo);

    void saveTransFlow(TransFlowBo transFlowBo);

    List<TransFlow> selectByUserId(Long userId);
}
