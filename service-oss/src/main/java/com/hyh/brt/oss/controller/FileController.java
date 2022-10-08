package com.hyh.brt.oss.controller;

import com.hyh.common.exception.BusinessException;
import com.hyh.common.result.R;
import com.hyh.common.result.ResponseEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

@RestController
//@CrossOrigin
@Api(tags = "腾讯云文件管理")
@RequestMapping("/api/oss/file")
public class FileController {

    @Resource
    private com.hyh.brt.oss.service.fileService fileService;

    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public R upload(
            @ApiParam(value= "文件", required = true)
            @RequestParam("file") MultipartFile file,

            @ApiParam(value = "模块", required = true)
            @RequestParam("module") String module){

        try {
            InputStream inputStream = file.getInputStream();
            String originalFilename = file.getOriginalFilename();
            String url = fileService.upload(inputStream, module, originalFilename);

            return R.ok().message("文件上传成功").data("url", url);
        } catch (IOException e) {
            throw new BusinessException(ResponseEnum.UPLOAD_ERROR, e);
        }
    }


    @ApiOperation("删除文件")
    @DeleteMapping("/remove")
    public R remove(
            @ApiParam(value = "要删除的文件",required = true)
            @RequestParam("url") String url)
    {
        fileService.removeFile(url);
        return R.ok().message("删除成功");
    }

}
