package com.xupt.yzh.dubboclient;

import com.xupt.yzh.ISayHelloService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DubboController {

    @Reference() // Dubbo提供的注解
    ISayHelloService sayHelloService;

    @GetMapping("/sayHello")
    public String sayHello() {
        return sayHelloService.sayHello();
    }
}
