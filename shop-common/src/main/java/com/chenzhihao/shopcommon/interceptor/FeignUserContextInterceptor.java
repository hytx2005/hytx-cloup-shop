package com.chenzhihao.shopcommon.interceptor;

import com.chenzhihao.shopcommon.util.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

/**
 * Feign interceptor for propagating user context (replaces DubboUserContextFilter)
 * @author dhx
 */
@Component
public class FeignUserContextInterceptor implements RequestInterceptor {

    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public void apply(RequestTemplate template) {
        // Get user ID from context and add to request header
        Long userId = UserContext.getUserId();
        if (userId != null) {
            template.header(USER_ID_HEADER, String.valueOf(userId));
        }
    }
}