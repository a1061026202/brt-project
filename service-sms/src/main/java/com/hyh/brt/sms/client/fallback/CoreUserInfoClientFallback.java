package com.hyh.brt.sms.client.fallback;

import com.hyh.brt.sms.client.CoreUserInfoClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CoreUserInfoClientFallback implements CoreUserInfoClient {
    @Override
    public boolean checkMobile(String mobile) {
        log.error("远程服务器失败,服务熔断");
        return false; //手机号码不重复
    }
}
