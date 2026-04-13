package com.chenzhihao.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置类
 *
 * 读取JWT相关的配置参数
 *
 * @author dhx
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    /**
     * JWT签名密钥
     */
    private String secretKey;
    /**
     * Token过期时间（毫秒）
     */
    private Long userTtl;
    /**
     * 前端请求中token的请求头名称
     */
    private String tokenName;
    /**
     * 向下游服务传递用户ID的请求头名称
     */
    private String claimName;
}
