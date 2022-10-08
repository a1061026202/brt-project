package com.hyh.brt.core;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

/*
断言简化异常处理
 */
public class AssertTest {

    @Test
    public void test(){
        Object o = null;
        if(o == null){
            throw new IllegalArgumentException("参数不能为空");
        }
    }

    @Test
    public void test1(){
        Object o = null;
        Assert.notNull(o,"参数不能为空");
    }

    @Test
    public void test2(){
        Boolean a = false;
        Assert.notNull(a,"结果为false");
    }
}
