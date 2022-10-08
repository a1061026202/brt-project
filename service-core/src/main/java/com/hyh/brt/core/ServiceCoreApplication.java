package com.hyh.brt.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Mr HE
 */
@SpringBootApplication
@ComponentScan({"com.hyh.brt","com.hyh.common"})
public class ServiceCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceCoreApplication.class,args);
    }
}
