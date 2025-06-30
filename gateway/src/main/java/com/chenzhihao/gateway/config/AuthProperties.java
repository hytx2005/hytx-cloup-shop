package com.chenzhihao.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 读取不需要进行登录校验的路径
 * @author dhx
 */
@Component
@Data
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
    private List<String> excludePaths;
}
