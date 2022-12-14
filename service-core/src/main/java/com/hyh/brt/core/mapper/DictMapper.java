package com.hyh.brt.core.mapper;

import com.hyh.brt.core.pojo.dto.ExcelDictDTO;
import com.hyh.brt.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 数据字典 Mapper 接口
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
public interface DictMapper extends BaseMapper<Dict> {

    void insertBatch(@Param("list") List<ExcelDictDTO> list);
}
