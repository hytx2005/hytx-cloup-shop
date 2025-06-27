package com.chenzhihao.products.dubbotest;

import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author ASUS
 */
@DubboService
public class DubboServiceImpl {
    public String testHello(String name) {
        return "hello " + name;
    }
}
