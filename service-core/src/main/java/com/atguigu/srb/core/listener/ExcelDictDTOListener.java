package com.atguigu.srb.core.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.srb.core.mapper.DictMapper;
import com.atguigu.srb.core.pojo.dto.ExcelDictDTO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class ExcelDictDTOListener extends AnalysisEventListener<ExcelDictDTO> {

     private DictMapper dictMapper;

     private List<ExcelDictDTO> list = new ArrayList<>();

     private static final int BATCH_COUNT=5;

     public ExcelDictDTOListener(DictMapper dictMapper) {
         this.dictMapper = dictMapper;
     }

    @Override
    public void invoke(ExcelDictDTO data, AnalysisContext analysisContext) {
        //将数据存储到list集合中
        list.add(data);

        //存到5条数据后执行存到数据库中
        if(list.size() >= BATCH_COUNT){
            save();
            list.clear();
        }
        log.info("解析到一条数据: {}",data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //将5的余数数据最后存储
        save();

        log.info("所有数据解析完成! ");
    }

    private void save(){
        log.info("{} 条数据存到数据库中",list.size());

        //批量保存
        dictMapper.insertBatch(list);
    }
}
