package com.chenzhihao.users;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@MapperScan(basePackages = {"com.chenzhihao.users.mapper"})
@ComponentScan(basePackages = {
        "com.chenzhihao.users",
        "com.chenzhihao.shopcommon.advice"
})
@EnableDiscoveryClient
public class UsersApplication {
    public static void main(String[] args) {
        SpringApplication.run(UsersApplication.class, args);
    }
}