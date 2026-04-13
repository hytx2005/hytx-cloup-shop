package com.chenzhihao.orders.service;

import com.chenzhihao.api.client.OrderClient.CreateOrderRequest;

/**
 * 内部订单服务接口
 * 用于处理服务间调用的订单业务逻辑
 *
 * @author dhx
 */
public interface IInternalOrderService {

    /**
     * 创建订单
     *
     * @param userId  用户ID
     * @param request 订单创建请求
     */
    void createOrder(Long userId, CreateOrderRequest request);
}
