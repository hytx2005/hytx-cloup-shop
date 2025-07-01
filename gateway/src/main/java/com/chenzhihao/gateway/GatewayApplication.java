package com.chenzhihao.gateway;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author ASUS
 */
@SpringBootApplication
@EnableDiscoveryClient
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