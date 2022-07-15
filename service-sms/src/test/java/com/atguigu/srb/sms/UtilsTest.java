package com.atguigu.srb.sms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UtilsTest {

    @Value("${ronglianyun.sms.auth-token}")
    private String auth;

    @Test
    public void test(){

        System.out.println(auth);

    }
}
