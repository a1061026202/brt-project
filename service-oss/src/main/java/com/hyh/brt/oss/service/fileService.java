package com.atguigu.srb.oss.service;

import java.io.InputStream;

public interface fileService {

    String upload(InputStream inputStream, String module, String fileName);

    void removeFile(String url);
}
