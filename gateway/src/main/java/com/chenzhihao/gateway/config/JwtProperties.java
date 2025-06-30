package com.chenzhihao.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取jwt相关配置
 * @author dhx
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    /**
     * 密钥
     */
    private String secretKey;
    /**
     * 过期时间
     */
    private Long userTtl;
    /**
     * 前端的令牌名称
     */
    private String tokenName;
    /**
     * 向其它服务传递的信息名称
     */
    private String claimName;
}
