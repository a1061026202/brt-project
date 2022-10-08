package com.hyh.brt.base.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class Swagger2Config {

    @Bean
    public Docket AdminApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("AdminApi")
                .apiInfo(AdminDesc())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/admin/.*")))
                .build();
    }

    private ApiInfo AdminDesc(){
        return new ApiInfoBuilder()
                .title("管理员接口文档")
                .description("文档描述了管理员接口的调用模块")
                .contact(new Contact("hyh","http://159.75.118.217","1061026202@qq.com"))
                .version("1.0").build();
    }



    @Bean
    public Docket WebApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("WebApi")
                .apiInfo(WebDesc())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/api/.*")))
                .build();
    }

    private ApiInfo WebDesc(){
        return new ApiInfoBuilder()
                .title("Web接口文档")
                .description("文档描述了Web接口的调用模块")
                .contact(new Contact("hyh","http://159.75.118.217","1061026202@qq.com"))
                .version("1.0").build();
    }
}
