package com.chenzhihao.shopcommon.util;

import cn.hutool.core.lang.UUID;

/**
 * 订单号工具类
 * @author dhx
 */
public class OrderNoUtil {

    /**
     * 生成订单号
     * @return {@link String }
     */
    public static String generateOrderNo() {
        // 获取当前时间戳
        long currentTimeMillis = System.currentTimeMillis();

        // 利用hutool工具生成UUID，替换掉其中的"-"
        String uuid = UUID.fastUUID().toString().replace("-","");

        // 拼接订单号
        return currentTimeMillis + uuid;
    }
}

