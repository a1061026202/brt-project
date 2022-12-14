package com.hyh.brt.core.service;

import com.hyh.brt.core.pojo.entity.BorrowerAttach;
import com.hyh.brt.core.pojo.vo.BorrowerAttachVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 借款人上传资源表 服务类
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
public interface BorrowerAttachService extends IService<BorrowerAttach> {

    List<BorrowerAttachVo> selectListById(Long id);

}
