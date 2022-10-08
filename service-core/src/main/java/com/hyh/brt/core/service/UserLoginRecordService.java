package com.hyh.brt.core.service;

import com.hyh.brt.core.pojo.entity.UserLoginRecord;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务类
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
public interface UserLoginRecordService extends IService<UserLoginRecord> {

    List<UserLoginRecord> listTop50(Long userId);
}
