package com.xupt.yzh.dubboprovider;

import com.xupt.yzh.ISayHelloService;
import org.apache.dubbo.config.annotation.Service;

@Service
public class SayHelloServiceImpl implements ISayHelloService {
    @Override
    public String sayHello() {
        return "Hello Dubbo...";
    }
}
