package com.chenzhihao.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 认证配置类
 *
 * 配置认证相关的白名单路径等
 *
 * @author dhx
 */
@Component
@Data
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
    /**
     * 不需要登录校验的路径白名单
     * 支持Ant路径匹配，如：/api/users/login, /api/products/**
     */
    private List<String> excludePaths;
}
