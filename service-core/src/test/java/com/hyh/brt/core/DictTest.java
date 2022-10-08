package com.hyh.brt.core;

import com.hyh.brt.core.mapper.DictMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class DictTest {
    @Resource
    private DictMapper dictMapper;

//    @Test
//    public void test(){
//        List<ExcelDictDTO> list = new ArrayList<>();
//        //ExcelDictDTO excelDictDTO = new ExcelDictDTO(2l,2l,"是否",20,"555");
//        list.add(excelDictDTO);
//        dictMapper.insertBatch(list);
//
//
//
//    }
}
