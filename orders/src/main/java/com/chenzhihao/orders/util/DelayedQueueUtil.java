package com.chenzhihao.orders.util;

import com.chenzhihao.orders.domain.po.Orders;
import com.chenzhihao.orders.mapper.OrdersMapper;
import com.chenzhihao.orders.service.IOrderTimeoutService;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * 延迟队列工具类 - 用于处理订单超时
 *
 * @author Claude
 */
@Component
public class DelayedQueueUtil {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private IOrderTimeoutService orderTimeoutService;

    private RBlockingQueue<String> orderTimeoutQueue;
    private RDelayedQueue<String> delayedQueue;

    private volatile boolean running = true;

    @PostConstruct
    public void init() {
        // 创建阻塞队列和延迟队列
        orderTimeoutQueue = redissonClient.getBlockingQueue("order:timeout:queue");
        delayedQueue = redissonClient.getDelayedQueue(orderTimeoutQueue);

        // 启动消费者线程
        startConsumer();
    }

    @PreDestroy
    public void destroy() {
        running = false;
        // Redisson queues are managed by Redisson client
        // No need to manually destroy them
    }

    /**
     * 添加订单到延迟队列（15分钟后超时）
     *
     * @param orderNo 订单号
     */
    public void addOrderToTimeoutQueue(String orderNo) {
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
        if (delayedQueue != null) {
            delayedQueue.remove(orderNo);
        }
    }

    /**
     * 启动消费者线程处理超时订单
     */
    private void startConsumer() {
        Thread consumerThread = new Thread(() -> {
            while (running) {
                try {
                    // 阻塞获取超时的订单号
                    String orderNo = orderTimeoutQueue.take();

                    if (orderNo != null) {
                        processTimeoutOrder(orderNo);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    // 记录错误日志，继续处理下一个
                    e.printStackTrace();
                }
            }
        });

        consumerThread.setName("OrderTimeout-Consumer");
        consumerThread.setDaemon(true);
        consumerThread.start();
    }

    /**
     * 处理超时订单
     *
     * @param orderNo 订单号
     */
    private void processTimeoutOrder(String orderNo) {
        try {
            // 查询订单当前状态
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Orders> queryWrapper =
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            queryWrapper.eq("order_no", orderNo);

            Orders order = ordersMapper.selectOne(queryWrapper);

            if (order != null && "PENDING".equals(order.getPayStatus())) {
                // 订单仍然处于待支付状态，执行取消操作
                orderTimeoutService.cancelPayment(orderNo);
                System.out.println("订单超时取消: " + orderNo);
            }
        } catch (Exception e) {
            // 处理异常，可以记录日志或发送告警
            e.printStackTrace();
        }
    }

    /**
     * 定期检查延迟队列状态（用于监控）
     */
    @Scheduled(fixedRate = 60000) // 每分钟检查一次
    public void monitorDelayedQueue() {
        if (delayedQueue != null) {
            long size = delayedQueue.size();
            System.out.println("延迟队列中待处理的订单数量: " + size);
        }
    }
}