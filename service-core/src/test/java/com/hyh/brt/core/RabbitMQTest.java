package com.hyh.brt.core;

import com.hyh.brt.base.dto.SmsDTO;
import com.hyh.brt.rabbitUtil.constant.MQConst;
import com.hyh.brt.rabbitUtil.service.MQService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class RabbitMQTest {

    @Resource
    MQService mqService;

    @Test
    public void send(){
        SmsDTO smsDTO = new SmsDTO();
        smsDTO.setMobile("13189985131");
        smsDTO.setMessage("充值成功");
        System.out.println(smsDTO);
        mqService.sendMessage(MQConst.EXCHANGE_TOPIC_SMS, MQConst.ROUTING_SMS_ITEM, smsDTO);
    }
}
