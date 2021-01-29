package com.xupt.yzh.testswagger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2 // 启动 Swagger2
public class SwaggerConfig {
    
    @Bean // 构建并配置 Docket 对象
    // 注：这里注入了 Environment 对象，目的是获取系统环境
    public Docket docket(Environment environment) {

        // 设置要显示 swagger 的环境（dev 或者 test）
        Profiles of = Profiles.of("dev", "test");
        // 判断当前是否处于该环境
        boolean flag = environment.acceptsProfiles(of);

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(flag)
                .useDefaultResponseMessages(false)
                .select() // 返回 ApiSelectorBuilder
                .apis(RequestHandlerSelectors.basePackage("com.xupt.yzh.testswagger.controller")) // 接口扫描方案
                .paths(PathSelectors.any()) // 接口路径过滤方案
                .build();

    }

    // 构建基本配置信息
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("商户投放子系统")
                .description("商户投放子系统接口文档")
                .version("v1.0")
                .termsOfServiceUrl("http://llxs.org")
                .contact(new Contact("A minor", "https://blog.csdn.net/weixin_43935927", "3132266573@qq.com"))
                .build();
    }

    @Bean
    public Docket docket2() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("打工人2组");
    }

    @Bean
    public Docket docket3() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("打工人3组");
    }
}