package com.atguigu.srb.core.service;

import com.atguigu.srb.core.pojo.entity.UserInfo;
import com.atguigu.srb.core.pojo.query.UserInfoQuery;
import com.atguigu.srb.core.pojo.vo.LoginVo;
import com.atguigu.srb.core.pojo.vo.RegisterVo;
import com.atguigu.srb.core.pojo.vo.UserIndexVo;
import com.atguigu.srb.core.pojo.vo.UserInfoVo;
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
