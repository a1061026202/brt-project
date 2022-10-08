package com.hyh.brt.core.service;

import com.hyh.brt.core.pojo.entity.UserBind;
import com.hyh.brt.core.pojo.vo.UserBindVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务类
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
public interface UserBindService extends IService<UserBind> {

    String commitBindUser(UserBindVo userBindVo, Long userId);

    void notify(Map<String, Object> paramMap);

    String getBindCodeByUserId(Long investUserId);
}
