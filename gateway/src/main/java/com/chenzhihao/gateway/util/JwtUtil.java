package com.chenzhihao.gateway.util;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.jwt.*;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * jwt工具类
 * @author dhx
 */
public class JwtUtil {
    /**
     * 生成token令牌
     * @param secretKey 密钥
     * @param ttlMillis 超时时间
     * @return {@link String }
     */
    public static String createToken(String secretKey, long ttlMillis, Long userId) {
        JWTSigner jwtSigner = JWTSignerUtil.hs256(secretKey.getBytes(StandardCharsets.UTF_8));
        return JWT.create()
                .setPayload("userId",userId)
                .setExpiresAt(new Date(System.currentTimeMillis() + ttlMillis))
                .setSigner(jwtSigner)
                .sign();
    }

    /**
     * 解析token令牌
     * @param secretKey 密钥
     * @param token token令牌
     * @return {@link Long }
     */
    public static Long parseToken(String secretKey,String token,String tokenName) {

        JWTSigner jwtSigner = JWTSignerUtil.hs256(secretKey.getBytes(StandardCharsets.UTF_8));

        JWT jwt;
        try {
            jwt = JWT.of(token).setSigner(jwtSigner);
        } catch (Exception e) {
            System.out.println("无效的token");
            throw new JWTException("无效的token");
        }
        // 2.校验jwt是否有效
        if (!jwt.verify()) {
            // 验证失败
            System.out.println("无效的token");
            throw new JWTException("无效的token");
        }
        // 3.校验是否过期
        try {
            JWTValidator.of(jwt).validateDate();
        } catch (ValidateException e) {
            System.out.println("token已经过期");
            throw new JWTException("token已经过期");
        }
        // 4.数据格式校验
        Object userPayload = jwt.getPayload(tokenName);
        if (userPayload == null) {
            // 数据为空
            System.out.println("无效的token");
            throw new JWTException("无效的token");
        }

        // 5.数据解析
        try {
            return Long.valueOf(userPayload.toString());
        } catch (RuntimeException e) {
            // 数据格式有误
            System.out.println("无效的token");
            throw new JWTException("无效的token");
        }
    }
}

