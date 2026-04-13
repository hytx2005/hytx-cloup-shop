package com.chenzhihao.shopcommon.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * 密码工具类
 *
 * 提供密码的加密和验证功能
 */
public class PasswordUtil {
    /**
     * 加密密码
     *
     * 使用BCrypt算法加密密码
     *
     * @param plainTextPassword 明文密码
     * @return 加密后的密码
     */
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * 验证密码
     *
     * 使用BCrypt算法验证密码
     *
     * @param plainTextPassword 明文密码
     * @param hashedPassword 加密后的密码
     * @return 验证成功返回true，否则返回false
     */
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
