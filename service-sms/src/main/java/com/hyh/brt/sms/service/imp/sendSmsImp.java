package com.hyh.brt.sms.service.imp;

import com.hyh.common.exception.Assert;
import com.hyh.common.result.ResponseEnum;
import com.hyh.brt.sms.service.sendSms;
import com.hyh.brt.sms.util.SmsProperties;
import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class sendSmsImp implements sendSms {

    @Override
    public void send(String mobile, Map<String, Object> param) {


        CCPRestSmsSDK sdk = new CCPRestSmsSDK();
        sdk.init(SmsProperties.SERVER_IP, SmsProperties.SERVER_PORT);
        sdk.setAccount(SmsProperties.ACCOUNT_ID, SmsProperties.AUTH_TOKEN);
        sdk.setAppId(SmsProperties.APP_ID);
        sdk.setBodyType(BodyType.Type_JSON);


        String[] datas = {(String)param.get("code"),"5"};

        HashMap<String, Object> result = sdk.sendTemplateSMS(mobile,SmsProperties.TEMPLATE_ID,datas);

        //HashMap<String, Object> result = sdk.sendTemplateSMS(mobile,SmsProperties.TEMPLATE_ID,datas,subAppend,reqId);
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for(String key:keySet){
                Object object = data.get(key);
                System.out.println(key +" = "+object);
            }
        }else{
            Assert.notEquals("160000",result.get("statusCode"), ResponseEnum.ALIYUN_RESPONSE_ERROR);
            Assert.notEquals("160038",result.get("statusCode"), ResponseEnum.ALIYUN_SMS_LIMIT_CONTROL_ERROR);
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
        }
    }
}
