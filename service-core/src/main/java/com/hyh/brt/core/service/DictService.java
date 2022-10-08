package com.hyh.brt.core.service;

import com.hyh.brt.core.pojo.dto.ExcelDictDTO;
import com.hyh.brt.core.pojo.entity.Dict;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 数据字典 服务类
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
public interface DictService extends IService<Dict> {

    void importData(InputStream inputStream);

    List<ExcelDictDTO> listDictData();

    List<Dict> listByParentId(Long parentId);

    List<Dict> findByDictCode(String dictCode);

    String getNameByParentDictCodeAndValues(String dictCode,Integer value);

}
