package com.atguigu.srb.core.service.impl;

import com.atguigu.srb.core.pojo.entity.BorrowerAttach;
import com.atguigu.srb.core.mapper.BorrowerAttachMapper;
import com.atguigu.srb.core.pojo.vo.BorrowerAttachVo;
import com.atguigu.srb.core.service.BorrowerAttachService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务实现类
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
@Service
public class BorrowerAttachServiceImpl extends ServiceImpl<BorrowerAttachMapper, BorrowerAttach> implements BorrowerAttachService {

    @Override
    public List<BorrowerAttachVo> selectListById(Long id) {
        QueryWrapper<BorrowerAttach> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("borrower_id",id);

        List<BorrowerAttach> borrowerAttaches = baseMapper.selectList(queryWrapper);

        List<BorrowerAttachVo> borrowerAttachVos = new ArrayList<>();
        borrowerAttaches.forEach(item -> {
            BorrowerAttachVo borrowerAttachVo = new BorrowerAttachVo();
            borrowerAttachVo.setImageType(item.getImageType());
            borrowerAttachVo.setImageUrl(item.getImageUrl());

            borrowerAttachVos.add(borrowerAttachVo);
        });

        return borrowerAttachVos;

    }
}
