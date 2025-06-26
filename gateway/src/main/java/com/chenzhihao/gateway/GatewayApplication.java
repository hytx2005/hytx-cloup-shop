package com.chenzhihao.gateway;

import com.alibaba.cloud.nacos.annotation.NacosConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
public class GatewayApplication implements InitializingBean{
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    private String s;
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(s);
    }
}