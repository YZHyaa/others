package com.xupt.yzh;

import org.apache.dubbo.container.Main;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class App {

//    public static void main(String[] args) throws IOException {
//        // 加载配置文件，即将相应服务 Bean实例化
//        ClassPathXmlApplicationContext classPathXmlApplicationContext =
//                new ClassPathXmlApplicationContext(new String[]{"META-INF.spring/application.xml"});
//        classPathXmlApplicationContext.start();
//        System.in.read();
//    }

    public static void main(String[] args) {
        Main.main(args);
    }
}
