package com.xupt.yzh;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {

    public static void main(String[] args) {
        // 加载配置文件，即将相应服务 Bean实例化
        ClassPathXmlApplicationContext classPathXmlApplicationContext =
                new ClassPathXmlApplicationContext(
                        new String[]{"META-INF.spring/application.xml"});
        // 拿到服务Bean
        IPayService payService = (IPayService)classPathXmlApplicationContext.getBean("PayService");
        String res = payService.pay("Test");
        System.out.println(res);

    }
}
