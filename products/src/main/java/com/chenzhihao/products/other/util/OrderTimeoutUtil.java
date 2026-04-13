package com.chenzhihao.products.other.util;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 订单超时处理工具类
 *
 * 替代Kafka的内存存储方案，配合XXL-Job处理订单超时
 *
 * 工作机制：
 * - 订单创建时，将订单商品信息存入内存Map
 * - XXL-Job定时任务扫描内存，检查订单是否超时
 * - 超时订单回滚库存并清理内存
 *
 * @author ASUS
 */
@Component
@Slf4j
public class OrderTimeoutUtil {

    /**
     * 订单商品信息映射
     * key: 订单号, value: 商品信息列表
     */
    public static final Map<String, List<OrderItem>> ORDER_MAP = new ConcurrentHashMap<>();

    /**
     * Redisson客户端，用于分布式锁
     */
    private static RedissonClient redissonClient;

    /**
     * 订单超时时间（分钟）
     */
    private static final long ORDER_TIMEOUT_MINUTES = 30;

    /**
     * 分布式锁前缀
     */
    private static final String ORDER_LOCK_PREFIX = "order_timeout_lock:";

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private RedisUtil redisUtil;

    @PostConstruct
    public void init() {
        redissonClient = this.redisson;
    }

    /**
     * 添加订单商品信息到内存
     *
     * 在订单创建时调用，记录订单的商品信息，用于超时回滚
     *
     * @param orderNo 订单号
     * @param commodityId 商品ID
     * @param num 购买数量
     */
    public static void addOrderItem(String orderNo, Long commodityId, Integer num) {
        OrderItem orderItem = OrderItem.builder()
                .orderNo(orderNo)
                .commodityId(commodityId)
                .num(num)
                .createTime(LocalDateTime.now().plusMinutes(ORDER_TIMEOUT_MINUTES))
                .build();

        RLock lock = redissonClient.getLock(ORDER_LOCK_PREFIX + orderNo);
        try {
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                ORDER_MAP.computeIfAbsent(orderNo, k -> new ArrayList<>())
                        .add(orderItem);
                log.info("订单商品信息已添加到内存: orderNo={}, commodityId={}, num={}",
                        orderNo, commodityId, num);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("添加订单商品信息时获取锁失败", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 检查并处理超时订单
     *
     * 由XXL-Job定时任务调用，检查订单是否超时
     *
     * 处理逻辑：
     * 1. 检查订单是否超过设定时间未支付
     * 2. 超时则回滚所有商品的库存
     * 3. 清理内存中的订单数据
     *
     * @param orderNo 订单号
     */
    public void updateRedisFromOrder(String orderNo) {
        List<OrderItem> orderItems = ORDER_MAP.get(orderNo);
        if (orderItems == null || orderItems.isEmpty()) {
            return;
        }

        RLock lock = redissonClient.getLock(ORDER_LOCK_PREFIX + orderNo);
        try {
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                LocalDateTime expiryTime = orderItems.get(0).getCreateTime();
                LocalDateTime now = LocalDateTime.now();

                if (expiryTime.isBefore(now)) {
                    // 订单已超时，回滚库存
                    for (OrderItem orderItem : orderItems) {
                        redisUtil.updateComPayNum(orderItem.getCommodityId(), orderItem.getNum());
                        log.info("订单超时，库存已回滚: orderNo={}, commodityId={}, num={}",
                                orderNo, orderItem.getCommodityId(), orderItem.getNum());
                    }
                    // 清理内存数据
                    ORDER_MAP.remove(orderNo);
                    log.info("超时订单已从内存中清理: orderNo={}", orderNo);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("处理超时订单时获取锁失败", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 订单商品信息
     */
    @lombok.Data
    @lombok.Builder
    public static class OrderItem {
        private String orderNo;        // 订单号
        private Long commodityId;      // 商品ID
        private Integer num;           // 购买数量
        private LocalDateTime createTime; // 超时时间
    }
}