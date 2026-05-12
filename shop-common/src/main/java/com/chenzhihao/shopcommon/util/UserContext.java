package com.chenzhihao.shopcommon.util;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 用户上下文工具类
 *
 * 使用TransmittableThreadLocal（TTL）在微服务间传递用户ID
 * 解决普通ThreadLocal在异步调用、线程池场景下传递失效的问题
 *
 * 工作流程：
 * 1. Gateway解析JWT获取userId，放入请求头
 * 2. UserInfoInterceptor从请求头读取userId存入TTL
 * 3. Feign调用时FeignUserContextInterceptor从TTL读取userId放入请求头
 * 4. 目标服务通过UserContextFilter从请求头读取userId存入TTL
 *
 * @author dhx
 */
public class UserContext {
    private static final TransmittableThreadLocal<Long> USER_ID = new TransmittableThreadLocal<>();

    /**
     * 设置当前线程的用户ID
     *
     * @param userId 用户ID
     */
    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    /**
     * 获取当前线程的用户ID
     *
     * @return 用户ID，未设置时返回null
     */
    public static Long getUserId() {
        return USER_ID.get();
    }

    /**
     * 移除当前线程的用户ID
     *
     * 防止内存泄漏，建议在请求处理完成后调用
     */
    public static void removeUserId() {
        USER_ID.remove();
    }
}
