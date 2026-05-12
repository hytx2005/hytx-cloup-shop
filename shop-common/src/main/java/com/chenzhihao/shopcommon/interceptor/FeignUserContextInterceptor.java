package com.chenzhihao.shopcommon.interceptor;

import com.chenzhihao.shopcommon.util.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

/**
 * Feign请求拦截器
 *
 * 在发起Feign调用时将当前用户ID传递给目标服务
 * 替代Dubbo的隐式传参机制
 *
 * @author dhx
 */
@Component
public class FeignUserContextInterceptor implements RequestInterceptor {

    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public void apply(RequestTemplate template) {
        // 从上下文获取用户ID并添加到请求头
        Long userId = UserContext.getUserId();
        if (userId != null) {
            template.header(USER_ID_HEADER, String.valueOf(userId));
        }
    }
}