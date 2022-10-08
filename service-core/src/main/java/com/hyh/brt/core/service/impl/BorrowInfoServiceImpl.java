package com.hyh.brt.core.service.impl;

import com.atguigu.common.exception.Assert;
import com.atguigu.common.result.ResponseEnum;
import com.hyh.brt.core.enums.BorrowInfoStatusEnum;
import com.hyh.brt.core.enums.BorrowerStatusEnum;
import com.hyh.brt.core.enums.UserBindEnum;
import com.hyh.brt.core.mapper.BorrowInfoMapper;
import com.hyh.brt.core.mapper.BorrowerMapper;
import com.hyh.brt.core.mapper.IntegralGradeMapper;
import com.hyh.brt.core.mapper.UserInfoMapper;
import com.hyh.brt.core.pojo.entity.BorrowInfo;
import com.hyh.brt.core.pojo.entity.Borrower;
import com.hyh.brt.core.pojo.entity.IntegralGrade;
import com.hyh.brt.core.pojo.entity.UserInfo;
import com.hyh.brt.core.pojo.vo.BorrowInfoApprovalVo;
import com.hyh.brt.core.pojo.vo.BorrowerDetailVo;
import com.hyh.brt.core.service.BorrowInfoService;
import com.hyh.brt.core.service.BorrowerService;
import com.hyh.brt.core.service.DictService;
import com.hyh.brt.core.service.LendService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 借款信息表 服务实现类
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
@Service
@Slf4j
public class BorrowInfoServiceImpl extends ServiceImpl<BorrowInfoMapper, BorrowInfo> implements BorrowInfoService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private IntegralGradeMapper integralGradeMapper;

    @Resource
    private DictService dictService;

    @Resource
    private BorrowerMapper borrowerMapper;

    @Resource
    private BorrowerService borrowerService;

    @Resource
    private LendService lendService;


    @Override
    public BigDecimal getBorrowAmount(Long userId) {
        //获取用户积分
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Assert.notNull(userInfo, ResponseEnum.LOGIN_MOBILE_ERROR);
        Integer integral = userInfo.getIntegral();

        //根据积分查询额度
        QueryWrapper<IntegralGrade> integralGradeQueryWrapper = new QueryWrapper<>();
        integralGradeQueryWrapper
                .le("integral_start", integral)
                .ge("integral_end", integral);
        IntegralGrade integralGrade = integralGradeMapper.selectOne(integralGradeQueryWrapper);
        log.info("查询结果: {}",integralGrade);
        if(integralGrade == null){
            return new BigDecimal("0");
        }

        return integralGrade.getBorrowAmount();
    }

    @Override
    public void saveBorrowInfo(BorrowInfo borrowInfo, Long userId) {
        //获取userInfo信息
        UserInfo userInfo = userInfoMapper.selectById(userId);

        //判断用户绑定状态
        Assert.isTrue(
                userInfo.getBindStatus().intValue() == UserBindEnum.BIND_OK.getStatus().intValue(),
                ResponseEnum.USER_NO_BIND_ERROR);

        //判断借款人额度申请状态
        Assert.isTrue(
                userInfo.getBorrowAuthStatus().intValue() == BorrowerStatusEnum.AUTH_OK.getStatus().intValue(),
                ResponseEnum.USER_NO_AMOUNT_ERROR);

        //判断借款人额度是否充足
        BigDecimal borrowAmount = this.getBorrowAmount(userId);
        Assert.isTrue(
                borrowInfo.getAmount().doubleValue() <= borrowAmount.doubleValue(),
                ResponseEnum.USER_AMOUNT_LESS_ERROR);


        //存储borrowInfo数据
        borrowInfo.setUserId(userId);
        //百分比转小数
        borrowInfo.setBorrowYearRate(borrowInfo.getBorrowYearRate().divide(new BigDecimal(100)));
        //设置借款申请的审核状态
        borrowInfo.setStatus(BorrowInfoStatusEnum.CHECK_RUN.getStatus());
        baseMapper.insert(borrowInfo);

    }

    @Override
    public Integer getStatusByUserId(Long userId) {
        QueryWrapper<BorrowInfo> borrowInfoQueryWrapper = new QueryWrapper<>();
        borrowInfoQueryWrapper.select("status").eq("user_id", userId);
        List<Object> objects = baseMapper.selectObjs(borrowInfoQueryWrapper);
        if(objects.size() == 0){
            return BorrowInfoStatusEnum.NO_AUTH.getStatus();
        }

        Integer status = (Integer)objects.get(0);
        return status;
    }

    @Override
    public List<BorrowInfo> selectList() {
        List<BorrowInfo> borrowInfoList = baseMapper.selectBorrowInfoList();
        borrowInfoList.forEach(borrowInfo -> {
            String returnMethod = dictService.getNameByParentDictCodeAndValues("returnMethod", borrowInfo.getReturnMethod());
            String moneyUse = dictService.getNameByParentDictCodeAndValues("moneyUse", borrowInfo.getMoneyUse());
            String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());

            borrowInfo.getParam().put("returnMethod", returnMethod);
            borrowInfo.getParam().put("moneyUse", moneyUse);
            borrowInfo.getParam().put("status", status);
        });

        return borrowInfoList;
    }

    @Override
    public Map<String, Object> getBorrowInfoDetail(Long id) {
        //查询借款对象：BorrowInfo
        BorrowInfo borrowInfo = baseMapper.selectById(id);
        String returnMethod = dictService.getNameByParentDictCodeAndValues("returnMethod", borrowInfo.getReturnMethod());
        String moneyUse = dictService.getNameByParentDictCodeAndValues("moneyUse", borrowInfo.getMoneyUse());
        String status = BorrowInfoStatusEnum.getMsgByStatus(borrowInfo.getStatus());
        borrowInfo.getParam().put("returnMethod", returnMethod);
        borrowInfo.getParam().put("moneyUse", moneyUse);
        borrowInfo.getParam().put("status", status);

        //查询借款人对象：Borrower(BorrowerDetailVO)
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.eq("user_id", borrowInfo.getUserId());
        Borrower borrower = borrowerMapper.selectOne(borrowerQueryWrapper);
        BorrowerDetailVo borrowerDetailVo = borrowerService.getBorrowerDetailVOById(borrower.getId());

        //组装集合结果
        Map<String, Object> result = new HashMap<>();
        result.put("borrowInfo", borrowInfo);
        result.put("borrower", borrowerDetailVo);
        return result;

    }

    @Override
    public void approval(BorrowInfoApprovalVo borrowInfoApprovalVo) {

        //修改借款审核的状态 borrow_info
        Long borrowInfoId = borrowInfoApprovalVo.getId();
        BorrowInfo borrowInfo = baseMapper.selectById(borrowInfoId);
        borrowInfo.setStatus(borrowInfoApprovalVo.getStatus());
        baseMapper.updateById(borrowInfo);

        //如果审核通过，则产生新的标的记录 lend
        if(borrowInfoApprovalVo.getStatus().intValue() == BorrowInfoStatusEnum.CHECK_OK.getStatus().intValue()){
            //创建新标的
            lendService.createLend(borrowInfoApprovalVo, borrowInfo);
        }
    }
}
