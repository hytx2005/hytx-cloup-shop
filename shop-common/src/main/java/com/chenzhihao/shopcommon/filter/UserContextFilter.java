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
 * Filter to extract user context from HTTP headers (for internal API calls)
 * @author dhx
 */
@Component
public class UserContextFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

        // Extract user ID from header for internal service calls
        String userIdHeader = request.getHeader(USER_ID_HEADER);
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                Long userId = Long.parseLong(userIdHeader);
                UserContext.setUserId(userId);
            } catch (NumberFormatException e) {
                // Log warning but continue processing
                System.out.println("Invalid user ID in header: " + userIdHeader);
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Clear user context after request processing
            UserContext.removeUserId();
        }
    }
}