package com.hyh.brt.core.service;

import com.hyh.brt.core.pojo.entity.LendReturn;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 还款记录表 服务类
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
public interface LendReturnService extends IService<LendReturn> {

    List<LendReturn> selectByLendId(Long lendId);

    String commitReturn(Long lendReturnId, Long userId);

    void notify(Map<String, Object> paramMap);
}
