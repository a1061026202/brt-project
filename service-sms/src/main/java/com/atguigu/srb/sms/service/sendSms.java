package com.atguigu.srb.sms.service;

import java.util.Map;

public interface sendSms {

    void send(String mobile, Map<String,Object> param);
}
