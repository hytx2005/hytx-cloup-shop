package com.chenzhihao.products;

import org.dromara.easyes.spring.annotation.EsMapperScan;
import org.mybatis.spring.annotation.MapperScan;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@MapperScan("com.chenzhihao.products.mapper.mp")
@EsMapperScan("com.chenzhihao.products.mapper.es")
/**
 * 商品模块启动类
 * @author dhx
 */
@MapperScan("com.chenzhihao.products.mapper")
@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
@ComponentScan(basePackages = {
        "com.chenzhihao.products",
        "com.chenzhihao.shopcommon"
})
public class ProductsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductsApplication.class, args);
    }
}