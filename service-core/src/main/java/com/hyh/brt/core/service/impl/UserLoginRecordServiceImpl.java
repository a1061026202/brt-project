package com.hyh.brt.core.service.impl;

import com.hyh.brt.core.pojo.entity.UserLoginRecord;
import com.hyh.brt.core.mapper.UserLoginRecordMapper;
import com.hyh.brt.core.service.UserLoginRecordService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户登录记录表 服务实现类
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
@Service
public class UserLoginRecordServiceImpl extends ServiceImpl<UserLoginRecordMapper, UserLoginRecord> implements UserLoginRecordService {

    @Override
    public List<UserLoginRecord> listTop50(Long userId) {
        QueryWrapper<UserLoginRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId)
                .orderByDesc("id")
                .last("limit 50");
        return baseMapper.selectList(queryWrapper);
    }
}
