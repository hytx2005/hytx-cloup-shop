package com.chenzhihao.shopcommon.util;

/**
 * 用来保存用户信息的ThreadLocal类
 * @author 线程类
 */
public class UserContext {
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
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
