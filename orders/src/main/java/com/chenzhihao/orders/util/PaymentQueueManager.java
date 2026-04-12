package com.chenzhihao.orders.util;

import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 支付队列管理器 - 负责延迟队列的添加和移除操作
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
     */
    public void initQueue() {
        if (delayedQueue == null) {
            delayedQueue = redissonClient.getDelayedQueue(redissonClient.getBlockingQueue("order:timeout:queue"));
        }
    }

    /**
     * 添加订单到延迟队列（15分钟后超时）
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
     * @param orderNo 订单号
     */
    public void removeOrderFromTimeoutQueue(String orderNo) {
        initQueue();
        if (delayedQueue != null) {
            delayedQueue.remove(orderNo);
        }
    }
}