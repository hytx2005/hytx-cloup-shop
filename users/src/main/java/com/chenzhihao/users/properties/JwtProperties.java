package com.chenzhihao.users.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取jwt相关配置
 * @author dhx
 */
@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {

    private String secretKey;

    private Long userTtl;

    private String tokenName;

    private String headerName;
}
