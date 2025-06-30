package com.chenzhihao.shopcommon.util;



import cn.hutool.jwt.Claims;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.signers.JWTSigner;
import cn.hutool.jwt.signers.JWTSignerUtil;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * jwt工具类
 * @author dhx
 */
public class JwtUtils {

    /**
     * 生成token令牌
     * @param secretKey 密钥
     * @param ttlMillis 超时时间
     * @param claims 数据
     * @return {@link String }
     */
    public static String createToken(String secretKey, long ttlMillis, Map<String, Object> claims) {
        JWTSigner jwtSigner = JWTSignerUtil.hs256(secretKey.getBytes(StandardCharsets.UTF_8));
        long now = System.currentTimeMillis();
        long exp = now + ttlMillis;

        claims.put("iat", now);
        claims.put("exp", exp);
        return JWTUtil.createToken(claims, jwtSigner);
    }

    /**
     * 解析token令牌
     * @param secretKey 密钥
     * @param token token令牌
     * @return {@link Claims }
     */
    public static Object parseToken(String secretKey,String token,String tokenName) {

        JWTSigner jwtSigner = JWTSignerUtil.hs256(secretKey.getBytes(StandardCharsets.UTF_8));
        if (JWTUtil.verify(token, jwtSigner)) {
            // 解析令牌
            JWT jwt = JWTUtil.parseToken(token);

            // 验证令牌是否过期
            if (jwt.validate(0)) {
                return jwt.getPayload().getClaim(tokenName);
            }
        }
        return null;
    }
}
