package com.chenzhihao.orders.util;

import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 支付队列管理器
 *
 * 封装延迟队列的添加和移除操作
 *
 * 职责：
 * - 管理延迟队列的初始化
 * - 提供统一的队列操作接口
 *
 * @author Claude
 */
@Component
public class PaymentQueueManager {

    @Autowired
    private RedissonClient redissonClient;

    private RDelayedQueue<String> delayedQueue;

    /**
     * 初始化延迟队列
     *
     * 延迟初始化，第一次使用时才创建
     */
    public void initQueue() {
        if (delayedQueue == null) {
            delayedQueue = redissonClient.getDelayedQueue(redissonClient.getBlockingQueue("order:timeout:queue"));
        }
    }

    /**
     * 添加订单到延迟队列（15分钟后超时）
     *
     * 在创建支付请求时调用，将订单号加入延迟队列
     *
     * @param orderNo 订单号
     */
    public void addOrderToTimeoutQueue(String orderNo) {
        initQueue();
        if (delayedQueue != null) {
            // 15分钟后触发
            delayedQueue.offer(orderNo, 15, TimeUnit.MINUTES);
        }
    }

    /**
     * 从延迟队列中移除订单（支付成功后调用）
     *
     * 在支付成功回调中调用，避免订单超时被取消
     *
     * @param orderNo 订单号
     */
    public void removeOrderFromTimeoutQueue(String orderNo) {
        initQueue();
        if (delayedQueue != null) {
            delayedQueue.remove(orderNo);
        }
    }
}