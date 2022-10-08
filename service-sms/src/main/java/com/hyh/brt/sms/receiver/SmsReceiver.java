package com.hyh.brt.sms.receiver;

import com.hyh.brt.base.dto.SmsDTO;
import com.hyh.brt.rabbitUtil.constant.MQConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;

@Component
@Slf4j
public class SmsReceiver {
    @Resource
    private com.hyh.brt.sms.service.sendSms sendSms;



    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MQConst.QUEUE_SMS_ITEM,durable = "true"),
            exchange = @Exchange(value = MQConst.EXCHANGE_TOPIC_SMS),
            key = {MQConst.ROUTING_SMS_ITEM}
    ))
    public void send(SmsDTO smsDTO) {
        log.info("SmsReceiver消息监听....");
        HashMap<String, Object> map = new HashMap<>();
        map.put("code",smsDTO.getMessage());
        sendSms.send(smsDTO.getMobile(),map);
    }
}
