package com.chenzhihao.orders;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@MapperScan("com.chenzhihao.orders.mapper")
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.chenzhihao.api.client")
@ComponentScan(basePackages = {
        "com.chenzhihao.orders",
        "com.chenzhihao.shopcommon"
})
public class OrdersApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrdersApplication.class, args);
    }
}