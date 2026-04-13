package com.chenzhihao.shopcommon.util;

import cn.hutool.core.lang.Snowflake;

/**
 * 订单号工具类
 *
 * 使用雪花算法生成分布式唯一订单号
 *
 * @author dhx
 */
public class OrderNoUtil {

    /**
     * 雪花算法实例
     * workerId: 工作机器ID(0~31)
     * datacenterId: 数据中心ID(0~31)
     */
    private static final Snowflake SNOWFLAKE = new Snowflake(1L, 1L);

    /**
     * 生成订单号
     *
     * 使用雪花算法生成分布式唯一ID
     *
     * @return 订单号
     */
    public static String generateOrderNo() {
        return String.valueOf(SNOWFLAKE.nextId());
    }

    /**
     * 初始化雪花算法
     *
     * 注意：此方法仅用于初始化，实际使用时建议通过配置注入
     *
     * @param workerId 工作机器ID(0~31)
     * @param datacenterId 数据中心ID(0~31)
     */
    public static void init(long workerId, long datacenterId) {
        // 注意：需要线程安全地重新初始化
        // 实际使用建议通过配置注入
    }
}

