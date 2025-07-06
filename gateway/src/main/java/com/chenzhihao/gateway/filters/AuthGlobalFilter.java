package com.chenzhihao.gateway.filters;

import cn.hutool.jwt.JWTException;
import com.chenzhihao.gateway.config.AuthProperties;
import com.chenzhihao.gateway.config.JwtProperties;
import com.chenzhihao.gateway.util.JwtUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

/**
 * 登录拦截器
 * @author ASUS
 */
@Component
@Data
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private AuthProperties authProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 1.获取request
        ServerHttpRequest request = exchange.getRequest();

        log.info("request:{}{}", request.getMethod(), request.getURI());

        // 2.判断是否需要登录拦截
        if (isExclude(request.getPath().toString())){
            log.info("不需要登录拦截");
            return chain.filter(exchange);
        }

        // 3.获取token
        List<String> headers = request.getHeaders().get(jwtProperties.getTokenName());
        String token = null;
        if (headers != null && !headers.isEmpty()){
            token = headers.get(0);
        }
        log.info("已经获取到token:{}", token);
        Long userId = null;
        try {
            userId  = JwtUtil.parseToken(jwtProperties.getSecretKey(), token, jwtProperties.getClaimName());
            log.info("userId:{}",userId);
        }catch (JWTException e){
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            log.error("token解析失败");
            return response.setComplete();
        }

        String userIdInfo = userId.toString();
        ServerWebExchange webExchange = exchange.mutate()
                .request(builder -> builder.header(jwtProperties.getClaimName(), userIdInfo))
                .build();

        // 放行
        log.info("已经放行");
        return chain.filter(webExchange);
    }

    private boolean isExclude(String path){
        for (String excludePath : authProperties.getExcludePaths()) {
            if (antPathMatcher.match(excludePath,path)){
                return true;
            }
        }
        return false;
    }
    @Override
    public int getOrder() {
        return 0;
    }
}
