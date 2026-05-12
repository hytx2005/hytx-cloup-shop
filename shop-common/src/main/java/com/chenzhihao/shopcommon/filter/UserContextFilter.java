package com.chenzhihao.shopcommon.filter;

import com.chenzhihao.shopcommon.util.UserContext;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户上下文过滤器
 *
 * 用于从HTTP请求头提取用户ID并设置到UserContext中
 * 主要处理微服务间Feign调用时的用户上下文传递
 *
 * 工作机制：
 * - 从X-User-Id请求头读取userId
 * - -存入TransmittableThreadLocal
 * - 请求处理完成后清理上下文，防止内存泄漏
 *
 * @author dhx
 */
@Component
public class UserContextFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

        // 从请求头中提取用户ID（用于内部服务调用）
        String userIdHeader = request.getHeader(USER_ID_HEADER);
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                Long userId = Long.parseLong(userIdHeader);
                UserContext.setUserId(userId);
            } catch (NumberFormatException e) {
                // 记录警告但继续处理请求
                System.out.println("Invalid user ID in header: " + userIdHeader);
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 请求处理完成后清理用户上下文
            UserContext.removeUserId();
        }
    }
}