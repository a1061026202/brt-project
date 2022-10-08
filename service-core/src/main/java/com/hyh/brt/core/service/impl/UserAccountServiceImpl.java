package com.hyh.brt.core.service.impl;

import com.atguigu.common.exception.Assert;
import com.atguigu.common.result.ResponseEnum;
import com.atguigu.srb.base.dto.SmsDTO;
import com.hyh.brt.core.enums.TransTypeEnum;
import com.hyh.brt.core.hfb.FormHelper;
import com.hyh.brt.core.hfb.HfbConst;
import com.hyh.brt.core.hfb.RequestHelper;
import com.hyh.brt.core.mapper.UserAccountMapper;
import com.hyh.brt.core.mapper.UserInfoMapper;
import com.hyh.brt.core.pojo.bo.TransFlowBo;
import com.hyh.brt.core.pojo.entity.UserAccount;
import com.hyh.brt.core.pojo.entity.UserInfo;
import com.hyh.brt.core.service.TransFlowService;
import com.hyh.brt.core.service.UserAccountService;
import com.hyh.brt.core.service.UserBindService;
import com.hyh.brt.core.service.UserInfoService;
import com.hyh.brt.core.util.LendNoUtils;
import com.atguigu.srb.rabbitUtil.constant.MQConst;
import com.atguigu.srb.rabbitUtil.service.MQService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>
 * 用户账户 服务实现类
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
@Service
@Slf4j
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private TransFlowService transFlowService;

    @Resource
    private UserBindService userBindService;

    @Resource
    private UserAccountService userAccountService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private MQService mqService;

    @Override
    public String commitCharge(BigDecimal chargeAmt, Long userId) {
        // 获取充值人绑定协议号
        UserInfo userInfo = userInfoMapper.selectById(userId);
        String bindCode = userInfo.getBindCode();

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getChargeNo());
        paramMap.put("bindCode", bindCode);
        paramMap.put("chargeAmt", chargeAmt);
        paramMap.put("feeAmt", new BigDecimal("0"));
        paramMap.put("notifyUrl", HfbConst.RECHARGE_NOTIFY_URL);
        paramMap.put("returnUrl", HfbConst.RECHARGE_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));

        String form = FormHelper.buildForm(HfbConst.RECHARGE_URL, paramMap);
        return form;
    }

    @Override
    public String notify(Map<String, Object> paramMap) {
        String agentBillNo = (String) paramMap.get("agentBillNo");

        //幂等性判断(判断流水是否存在)
        boolean isSave = transFlowService.isSaveTranFlow(agentBillNo);
        if (isSave){
            log.warn("幂等性返回");
            return "success";
        }


        //账户处理
        String bindCode = (String)paramMap.get("bindCode");
        String chargeAmt = (String)paramMap.get("chargeAmt");
        baseMapper.updateAccount(bindCode,new BigDecimal(chargeAmt),new BigDecimal(0));



        //记录账户流水
        TransFlowBo transFlowBo = new TransFlowBo(
                agentBillNo,
                bindCode,
                new BigDecimal(chargeAmt),
                TransTypeEnum.RECHARGE,
                "充值啦"
        );
        transFlowService.saveTransFlow(transFlowBo);

        //发消息
        SmsDTO smsDTO = new SmsDTO();
        String mobileByBindCode = userInfoService.getMobileByBindCode(bindCode);
        smsDTO.setMobile(mobileByBindCode);
        smsDTO.setMessage("充值成功");
        mqService.sendMessage(
                MQConst.EXCHANGE_TOPIC_SMS,
                MQConst.ROUTING_SMS_ITEM,
                smsDTO
        );
        return "success";

    }

    @Override
    public BigDecimal getAccount(Long userId) {
        QueryWrapper<UserAccount> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("user_id", userId);
        UserAccount userAccount = baseMapper.selectOne(userAccountQueryWrapper);
        return userAccount.getAmount();
    }

    @Override
    public String commitWithdraw(BigDecimal fetchAmt, Long userId) {
        //用户账户余额
        BigDecimal amount = userAccountService.getAccount(userId);
        Assert.isTrue(amount.doubleValue() >= fetchAmt.doubleValue(),
                ResponseEnum.NOT_SUFFICIENT_FUNDS_ERROR);

        String bindCode = userBindService.getBindCodeByUserId(userId);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId", HfbConst.AGENT_ID);
        paramMap.put("agentBillNo", LendNoUtils.getWithdrawNo());
        paramMap.put("bindCode", bindCode);
        paramMap.put("fetchAmt", fetchAmt);
        paramMap.put("feeAmt", new BigDecimal(0));
        paramMap.put("notifyUrl", HfbConst.WITHDRAW_NOTIFY_URL);
        paramMap.put("returnUrl", HfbConst.WITHDRAW_RETURN_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        String sign = RequestHelper.getSign(paramMap);
        paramMap.put("sign", sign);

        //构建自动提交表单
        String formStr = FormHelper.buildForm(HfbConst.WITHDRAW_URL, paramMap);
        return formStr;
    }

    @Override
    public void notifyWithdraw(Map<String, Object> paramMap) {
        //幂等判断
        log.info("提现成功");
        String agentBillNo = (String)paramMap.get("agentBillNo");
        boolean result = transFlowService.isSaveTranFlow(agentBillNo);
        if(result){
            log.warn("幂等性返回");
            return;
        }

        //账户同步
        String bindCode = (String)paramMap.get("bindCode");
        String fetchAmt = (String)paramMap.get("fetchAmt");
        baseMapper.updateAccount(bindCode, new BigDecimal("-" + fetchAmt), new BigDecimal(0));

        //交易流水
        TransFlowBo transFlowBO = new TransFlowBo(
                agentBillNo,
                bindCode,
                new BigDecimal(fetchAmt),
                TransTypeEnum.WITHDRAW,
                "提现啦");
        transFlowService.saveTransFlow(transFlowBO);
    }
}
