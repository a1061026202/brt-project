package com.hyh.brt.core.service.impl;

import com.alibaba.excel.EasyExcel;
import com.hyh.brt.core.listener.ExcelDictDTOListener;
import com.hyh.brt.core.mapper.DictMapper;
import com.hyh.brt.core.pojo.dto.ExcelDictDTO;
import com.hyh.brt.core.pojo.entity.Dict;
import com.hyh.brt.core.service.DictService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 数据字典 服务实现类
 * </p>
 *
 * @author hyh
 * @since 2022-06-19
 */
@Service
@Slf4j
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {


    @Resource
    private RedisTemplate redisTemplate;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importData(InputStream inputStream) {
        EasyExcel.read(inputStream, ExcelDictDTO.class,new ExcelDictDTOListener(baseMapper)).sheet().doRead();
        log.info("excel导入成功");
    }

    @Override
    public List<ExcelDictDTO> listDictData() {
        List<Dict> dictList = baseMapper.selectList(null);
        List<ExcelDictDTO> excelDictDTOS = new ArrayList<>(dictList.size());

        dictList.forEach(dict -> {
            ExcelDictDTO excelDictDTO = new ExcelDictDTO();
            BeanUtils.copyProperties(dict,excelDictDTO);
            excelDictDTOS.add(excelDictDTO);
        });

        return excelDictDTOS;
    }

    @Override
    public List<Dict> listByParentId(Long parentId) {

        //首先查询redis中是否存在数据列表
        try {
            List<Dict> dictList = (List<Dict>)redisTemplate.opsForValue().get("srb:core:dictList:" + parentId);
            if(dictList != null){
                log.info("从redis取出数据");
                //如果存在就从redis中直接返回
                return dictList;
            }


        } catch (Exception e) {
            log.error("redis服务器异常: "+ ExceptionUtils.getStackTrace(e));
        }


        //如果redis不存在就查询数据库
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id",parentId);

        List<Dict> dictList = baseMapper.selectList(dictQueryWrapper);

        dictList.forEach(item ->{
            //判断当前节点是否有子节点,并找到子节点
            boolean hasChildren = this.hasChildren(item.getId());
            item.setHasChildren(hasChildren);
        });


        //将数据库数据存入redis
        try {
            log.info("将数据存入redis");
            redisTemplate.opsForValue().set("srb:core:dictList:"+parentId,dictList,7, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("redis服务器异常: "+ ExceptionUtils.getStackTrace(e));
        }

        //返回数据列表
        return dictList;
    }

    @Override
    public List<Dict> findByDictCode(String dictCode) {

        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("dict_code", dictCode);
        Dict dict = baseMapper.selectOne(dictQueryWrapper);
        return this.listByParentId(dict.getId());
    }

    @Override
    public String getNameByParentDictCodeAndValues(String dictCode,Integer value) {
        // 获取dict_code信息
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("dict_code",dictCode);
        Dict parentDict = baseMapper.selectOne(dictQueryWrapper);

        if(parentDict == null){
            return "";
        }

        dictQueryWrapper = new QueryWrapper<>();

        dictQueryWrapper
                .eq("parent_id",parentDict.getId())
                .eq("value",value);
        Dict dict = baseMapper.selectOne(dictQueryWrapper);

        if(dict == null){
            return "";
        }

        return dict.getName();


    }

    /**
     * 判断当前id所在的节点下是否有子节点
     * @param id
     * @return
     */
    private boolean hasChildren(Long id){
        QueryWrapper<Dict> dictQueryWrapper = new QueryWrapper<>();
        dictQueryWrapper.eq("parent_id", id);
        Integer count = baseMapper.selectCount(dictQueryWrapper);
        if(count.intValue() > 0){
            return true;
        }
        return false;
    }

}
