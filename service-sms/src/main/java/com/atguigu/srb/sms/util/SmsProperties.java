package com.atguigu.srb.sms.util;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ronglianyun.sms")
@Data
public class SmsProperties implements InitializingBean {
//
//    account-id: 8a216da881ad97540181ae18dac00007
//    auth-token: f12f8019dc3e4be1b8b2603f9ae2c16d
//    app-id: 8a216da881ad97540181ae18dc25000e
//    template-id: SMS
//    server-ip: app.cloopen.com
//    server-port: 8883

    private String accountId;
    private String authToken;
    private String appId;
    private String templateId;
    private String serverIp;
    private String serverPort;

    public static String ACCOUNT_ID;
    public static String AUTH_TOKEN;
    public static String APP_ID;
    public static String TEMPLATE_ID;
    public static String SERVER_IP;
    public static String SERVER_PORT;


    @Override
    public void afterPropertiesSet() throws Exception {
        ACCOUNT_ID = accountId;
        AUTH_TOKEN = authToken;
        APP_ID = appId;
        TEMPLATE_ID = templateId;
        SERVER_IP = serverIp;
        SERVER_PORT = serverPort;
    }
}
