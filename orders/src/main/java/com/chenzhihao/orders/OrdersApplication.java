package com.chenzhihao.orders;

import org.mybatis.spring.annotation.MapperScan;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@MapperScan("com.chenzhihao.orders.mapper")
@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
@ComponentScan(basePackages = {
        "com.chenzhihao.orders",
        "com.chenzhihao.shopcommon"
})
public class OrdersApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrdersApplication.class, args);
    }
}