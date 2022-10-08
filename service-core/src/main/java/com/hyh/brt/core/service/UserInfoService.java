package com.hyh.brt.core.service;

import com.hyh.brt.core.pojo.entity.UserInfo;
import com.hyh.brt.core.pojo.query.UserInfoQuery;
import com.hyh.brt.core.pojo.vo.LoginVo;
import com.hyh.brt.core.pojo.vo.RegisterVo;
import com.hyh.brt.core.pojo.vo.UserIndexVo;
import com.hyh.brt.core.pojo.vo.UserInfoVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
public interface UserInfoService extends IService<UserInfo> {

    void register(RegisterVo registerVo);

    UserInfoVo login(LoginVo loginVo, String ip);

    IPage<UserInfo> listPage(Page<UserInfo> pageParam, UserInfoQuery userInfoQuery);

    void lock(Long id, Integer status);

    boolean checkMobile(String mobile);

    UserIndexVo getIndexUserInfo(Long userId);

    String getMobileByBindCode(String bindCode);
}
