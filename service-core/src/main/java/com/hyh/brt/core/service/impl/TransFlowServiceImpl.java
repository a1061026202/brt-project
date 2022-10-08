package com.hyh.brt.core.service.impl;

import com.hyh.brt.core.mapper.TransFlowMapper;
import com.hyh.brt.core.mapper.UserInfoMapper;
import com.hyh.brt.core.pojo.bo.TransFlowBo;
import com.hyh.brt.core.pojo.entity.TransFlow;
import com.hyh.brt.core.pojo.entity.UserInfo;
import com.hyh.brt.core.service.TransFlowService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 交易流水表 服务实现类
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
@Service
public class TransFlowServiceImpl extends ServiceImpl<TransFlowMapper, TransFlow> implements TransFlowService {


    @Resource
    private UserInfoMapper userInfoMapper;

    @Override
    public boolean isSaveTranFlow(String agentBillNo) {
        QueryWrapper<TransFlow> transFlowQueryWrapper = new QueryWrapper<>();
        transFlowQueryWrapper.eq("trans_no",agentBillNo);

        Integer count = baseMapper.selectCount(transFlowQueryWrapper);
        return count>0;
    }

    @Override
    public void saveTransFlow(TransFlowBo transFlowBo) {
        String bindCode = transFlowBo.getBindCode();
        QueryWrapper<UserInfo> userInfoQueryWrapper = new QueryWrapper<>();
        userInfoQueryWrapper.eq("bind_code", bindCode);
        UserInfo userInfo = userInfoMapper.selectOne(userInfoQueryWrapper);

        TransFlow transFlow = new TransFlow();
        transFlow.setTransAmount(transFlowBo.getAmount());
        transFlow.setMemo(transFlowBo.getMemo());
        transFlow.setTransTypeName(transFlowBo.getTransTypeEnum().getTransTypeName());
        transFlow.setTransType(transFlowBo.getTransTypeEnum().getTransType());
        transFlow.setTransNo(transFlowBo.getAgentBillNo());//流水号
        transFlow.setUserId(userInfo.getId());
        transFlow.setUserName(userInfo.getName());
        baseMapper.insert(transFlow);
    }

    @Override
    public List<TransFlow> selectByUserId(Long userId) {

        QueryWrapper<TransFlow> transFlowQueryWrapper = new QueryWrapper<>();
        transFlowQueryWrapper.eq("user_id", userId).orderByDesc("id");
        return baseMapper.selectList(transFlowQueryWrapper);
    }
}
