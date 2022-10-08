package com.hyh.brt.oss.util;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "txy.oss")
public class OssProperties implements InitializingBean {

//    endpoint: srb-1253645988.cos.ap-guangzhou.myqcloud.com
//    secret-id: LTAI4GK
//    bucket-name: srb-1253645988
//    secret-key: 7bq

    private String endpoint;
    private String secretId;
    private String SecretKey;
    private String bucketName;

    public static String ENDPOINT;
    public static String SECRET_ID;
    public static String SECRET_KEY;
    public static String BUCKET_NAME;

    @Override
    public void afterPropertiesSet() throws Exception {
        ENDPOINT = endpoint;
        SECRET_ID = secretId;
        SECRET_KEY = SecretKey;
        BUCKET_NAME = bucketName;
    }
}
