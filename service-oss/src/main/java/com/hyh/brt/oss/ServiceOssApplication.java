package com.hyh.brt.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Mr HE
 */
@SpringBootApplication
@ComponentScan({"com.hyh.brt","com.hyh.common"})
public class ServiceOssApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceOssApplication.class,args);
    }
}
