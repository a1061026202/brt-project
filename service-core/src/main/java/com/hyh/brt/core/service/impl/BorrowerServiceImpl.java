package com.hyh.brt.core.service.impl;

import com.hyh.brt.core.enums.BorrowerStatusEnum;
import com.hyh.brt.core.enums.IntegralEnum;
import com.hyh.brt.core.mapper.BorrowerAttachMapper;
import com.hyh.brt.core.mapper.BorrowerMapper;
import com.hyh.brt.core.mapper.UserInfoMapper;
import com.hyh.brt.core.mapper.UserIntegralMapper;
import com.hyh.brt.core.pojo.entity.Borrower;
import com.hyh.brt.core.pojo.entity.BorrowerAttach;
import com.hyh.brt.core.pojo.entity.UserInfo;
import com.hyh.brt.core.pojo.entity.UserIntegral;
import com.hyh.brt.core.pojo.vo.BorrowerApprovalVo;
import com.hyh.brt.core.pojo.vo.BorrowerAttachVo;
import com.hyh.brt.core.pojo.vo.BorrowerDetailVo;
import com.hyh.brt.core.pojo.vo.BorrowerVo;
import com.hyh.brt.core.service.BorrowerAttachService;
import com.hyh.brt.core.service.BorrowerService;
import com.hyh.brt.core.service.DictService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
@Service
@Slf4j
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {

    @Resource
    private UserInfoMapper userInfoMapper;

    @Resource
    private BorrowerAttachMapper borrowerAttachMapper;

    @Resource
    private DictService dictService;

    @Resource
    private BorrowerAttachService borrowerAttachService;

    @Resource
    private UserIntegralMapper userIntegralMapper;



    @Override
    public void saveBorrowerVoByUserId(BorrowerVo borrowerVo, Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(borrowerVo,borrower);

        borrower.setUserId(userId);
        borrower.setName(userInfo.getName());
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setMobile(userInfo.getMobile());
        borrower.setStatus(BorrowerStatusEnum.AUTH_RUN.getStatus()); //认证中
        baseMapper.insert(borrower);

        //保存附件
        List<BorrowerAttach> borrowerAttachList = borrowerVo.getBorrowerAttachList();
        borrowerAttachList.forEach(borrowerAttach -> {
            borrowerAttach.setBorrowerId(borrower.getId());
            borrowerAttachMapper.insert(borrowerAttach);
        });

        //更新userInfo中的借款人认证状态
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public Integer getStatusByUserId(Long userId) {
        QueryWrapper<Borrower> borrowerQueryWrapper = new QueryWrapper<>();
        borrowerQueryWrapper.select("status").eq("user_id", userId);
        List<Object> objects = baseMapper.selectObjs(borrowerQueryWrapper);
        if(objects.size() == 0){
            return BorrowerStatusEnum.NO_AUTH.getStatus();
        }

        Integer status = (Integer)objects.get(0);
        return status;
    }

    @Override
    public IPage<Borrower> listPage(Page<Borrower> borrowerPage, String keyword) {
        if(StringUtils.isBlank(keyword)){
            return baseMapper.selectPage(borrowerPage,null);
        }
        QueryWrapper<Borrower> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .like("name",keyword)
                .or().like("id_card",keyword)
                .or().like("mobile",keyword)
                .orderByDesc("id");
        return baseMapper.selectPage(borrowerPage,queryWrapper);
    }

    @Override
    public BorrowerDetailVo getBorrowerDetailVOById(Long id) {
         // 获取借款人信息
        Borrower borrower = baseMapper.selectById(id);

        log.info("borrow: {}",borrower);

        //填充基本借款人信息
        BorrowerDetailVo borrowerDetailVo = new BorrowerDetailVo();
        BeanUtils.copyProperties(borrower,borrowerDetailVo);

        //性别
        borrowerDetailVo.setSex(borrower.getSex()==1?"男":"女");
        //婚否
        borrowerDetailVo.setMarry(borrower.getMarry()?"是":"否");

        // 填充其他信息
        borrowerDetailVo.setEducation(dictService.getNameByParentDictCodeAndValues("education",borrower.getEducation()));
        borrowerDetailVo.setIndustry(dictService.getNameByParentDictCodeAndValues("industry",borrower.getIndustry()));
        borrowerDetailVo.setIncome(dictService.getNameByParentDictCodeAndValues("income",borrower.getIncome()));
        borrowerDetailVo.setReturnSource(dictService.getNameByParentDictCodeAndValues("returnSource",borrower.getReturnSource()));
        borrowerDetailVo.setContactsRelation(dictService.getNameByParentDictCodeAndValues("relation", borrower.getContactsRelation()));

        //审批状态
        String status = BorrowerStatusEnum.getMsgByStatus(borrower.getStatus());
        borrowerDetailVo.setStatus(status);

        // 填充附件
        List<BorrowerAttachVo> borrowerAttachVos = borrowerAttachService.selectListById(id);
        borrowerDetailVo.setBorrowerAttachVOList(borrowerAttachVos);

        log.info("borrowerDetailVo: {}",borrowerDetailVo);

        return borrowerDetailVo;

    }

    @Override
    public void approval(BorrowerApprovalVo borrowerApprovalVo) {
        //获取借款额度申请id
        Long borrowerId = borrowerApprovalVo.getBorrowerId();

        //获取借款额度申请对象
        Borrower borrower = baseMapper.selectById(borrowerId);

        //设置审核状态
        borrower.setStatus(borrowerApprovalVo.getStatus());
        baseMapper.updateById(borrower);

        //获取用户id
        Long userId = borrower.getUserId();

        //获取用户对象
        UserInfo userInfo = userInfoMapper.selectById(userId);

        //用户的原始积分
        Integer integral = userInfo.getIntegral();

        //计算基本信息积分
        UserIntegral userIntegral = new UserIntegral();
        userIntegral.setUserId(userId);
        userIntegral.setIntegral(borrowerApprovalVo.getInfoIntegral());
        userIntegral.setContent("借款人基本信息");
        userIntegralMapper.insert(userIntegral);
        int currentIntegral = integral + borrowerApprovalVo.getInfoIntegral();

        //身份证积分
        if(borrowerApprovalVo.getIsIdCardOk()){
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_IDCARD.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_IDCARD.getMsg());
            userIntegralMapper.insert(userIntegral);
            currentIntegral += IntegralEnum.BORROWER_IDCARD.getIntegral();
        }

        //房产积分
        if(borrowerApprovalVo.getIsHouseOk()){
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_HOUSE.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_HOUSE.getMsg());
            userIntegralMapper.insert(userIntegral);
            currentIntegral += IntegralEnum.BORROWER_HOUSE.getIntegral();
        }

        //车辆积分
        if(borrowerApprovalVo.getIsCarOk()){
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_CAR.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_CAR.getMsg());
            userIntegralMapper.insert(userIntegral);
            currentIntegral += IntegralEnum.BORROWER_CAR.getIntegral();
        }

        //设置用户总积分
        userInfo.setIntegral(currentIntegral);

        //修改审核状态
        userInfo.setBorrowAuthStatus(borrowerApprovalVo.getStatus());

        //更新userInfo
        userInfoMapper.updateById(userInfo);
    }
}
