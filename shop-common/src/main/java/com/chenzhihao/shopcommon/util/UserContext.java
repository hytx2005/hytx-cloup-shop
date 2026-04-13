package com.chenzhihao.shopcommon.util;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * 用来保存用户信息的ThreadLocal类
 * 使用TransmittableThreadLocal支持线程池环境
 * @author 线程类
 */
public class UserContext {
    private static final TransmittableThreadLocal<Long> USER_ID = new TransmittableThreadLocal<>();
    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }
    public static Long getUserId() {
        return USER_ID.get();
    }
    public static void removeUserId() {
        USER_ID.remove();
    }
}
