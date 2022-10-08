package com.hyh.brt.core.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.hyh.common.exception.BusinessException;
import com.hyh.common.result.R;
import com.hyh.common.result.ResponseEnum;
import com.hyh.brt.core.pojo.dto.ExcelDictDTO;
import com.hyh.brt.core.pojo.entity.Dict;
import com.hyh.brt.core.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

@Api(tags = "数据字典")
@Slf4j
@RestController
//@CrossOrigin
@RequestMapping("/admin/core/dict")
public class AdminDictController {

    @Resource
    DictService dictService;

    @ApiOperation("Excel数据的批量导入")
    @PostMapping("/import")
    public R batchImport(
            @ApiParam(value = "Excel数据字典文件",required = true)
            @RequestParam("file") MultipartFile file){
        try {
            InputStream inputStream = file.getInputStream();
            log.info("{}",inputStream);
            dictService.importData(inputStream);

            return R.ok().message("数据批量导入成功");
        } catch (Exception e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR,e);
        }
    }

    @ApiOperation("Excel数据导出")
    @GetMapping("/export")
    public void download(HttpServletResponse response) throws IOException {
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("myDict", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), ExcelDictDTO.class).sheet("模板").doWrite(dictService.listDictData());
    }

    @ApiOperation("根据上级id获取子节点数据列表")
    @GetMapping("/listByParentId/{parentId}")
    public R ListByParentId(
            @ApiParam(value = "上级节点id",required = true)
            @PathVariable Long parentId)
    {

        List<Dict> dictList = dictService.listByParentId(parentId);
        return R.ok().data("list",dictList);
    }

}
