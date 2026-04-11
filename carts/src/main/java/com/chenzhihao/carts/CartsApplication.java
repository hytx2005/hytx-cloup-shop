package com.chenzhihao.carts;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@MapperScan("com.chenzhihao.carts.mapper")
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = {
        "com.chenzhihao.carts",
        "com.chenzhihao.shopcommon"
})
public class CartsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CartsApplication.class, args);
    }

}
