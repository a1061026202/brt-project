package com.hyh.brt.core.service.impl;

import com.hyh.common.exception.Assert;
import com.hyh.common.result.ResponseEnum;
import com.hyh.brt.core.enums.UserBindEnum;
import com.hyh.brt.core.hfb.FormHelper;
import com.hyh.brt.core.hfb.HfbConst;
import com.hyh.brt.core.hfb.RequestHelper;
import com.hyh.brt.core.mapper.UserBindMapper;
import com.hyh.brt.core.mapper.UserInfoMapper;
import com.hyh.brt.core.pojo.entity.UserBind;
import com.hyh.brt.core.pojo.entity.UserInfo;
import com.hyh.brt.core.pojo.vo.UserBindVo;
import com.hyh.brt.core.service.UserBindService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
@Service
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {

    @Resource
    private UserInfoMapper userInfoMapper;
    @Override
    public String commitBindUser(UserBindVo userBindVo, Long userId) {

        //不同的user_id, 相同的身份证，如果存在，则不允许
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper
                .eq("id_card", userBindVo.getIdCard())
                .ne("user_id", userId);
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        Assert.isNull(userBind, ResponseEnum.USER_BIND_IDCARD_EXIST_ERROR);

        //用户是否曾经填写过绑定表单
        userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id", userId);
        userBind = baseMapper.selectOne(userBindQueryWrapper);

        if(userBind == null){
            //创建用户绑定记录
            userBind = new UserBind();
            BeanUtils.copyProperties(userBindVo, userBind);
            userBind.setUserId(userId);
            userBind.setStatus(UserBindEnum.NO_BIND.getStatus());
            baseMapper.insert(userBind);
        }else{
            //相同的user_id，如果存在，那么就取出数据，做更新
            BeanUtils.copyProperties(userBindVo, userBind);
            baseMapper.updateById(userBind);
        }


        Map<String, Object> userBindVoMap = new HashMap<>();
        userBindVoMap.put("agentId", HfbConst.AGENT_ID);
        userBindVoMap.put("agentUserId", userId);
        userBindVoMap.put("idCard",userBindVo.getIdCard());
        userBindVoMap.put("personalName", userBindVo.getName());
        userBindVoMap.put("bankType", userBindVo.getBankType());
        userBindVoMap.put("bankNo", userBindVo.getBankNo());
        userBindVoMap.put("mobile", userBindVo.getMobile());
        userBindVoMap.put("returnUrl", HfbConst.USERBIND_RETURN_URL);
        userBindVoMap.put("notifyUrl", HfbConst.USERBIND_NOTIFY_URL);
        userBindVoMap.put("timestamp", RequestHelper.getTimestamp());
        userBindVoMap.put("sign", RequestHelper.getSign(userBindVoMap));


        // 生成动态表单字符串
        String form = FormHelper.buildForm(HfbConst.USERBIND_URL, userBindVoMap);
        return form;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void notify(Map<String, Object> paramMap) {
        String bindCode = (String)paramMap.get("bindCode");
        String agentUserId = (String)paramMap.get("agentUserId");

        //根据user_id查询user_bind记录
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id", agentUserId);

        //更新用户绑定表
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        userBind.setBindCode(bindCode);
        userBind.setStatus(UserBindEnum.BIND_OK.getStatus());
        baseMapper.updateById(userBind);

        //更新用户表
        UserInfo userInfo = userInfoMapper.selectById(agentUserId);
        userInfo.setBindCode(bindCode);
        userInfo.setName(userBind.getName());
        userInfo.setIdCard(userBind.getIdCard());
        userInfo.setBindStatus(UserBindEnum.BIND_OK.getStatus());
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public String getBindCodeByUserId(Long investUserId) {
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id", investUserId);
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        return userBind.getBindCode();
    }
}
