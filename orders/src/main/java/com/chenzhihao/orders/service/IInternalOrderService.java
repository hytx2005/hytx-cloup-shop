package com.chenzhihao.orders.service;

import com.chenzhihao.api.client.OrderClient.CreateOrderRequest;

/**
 * 内部订单服务接口
 * 用于处理服务间调用的订单业务逻辑，提供订单创建功能
 *
 * @author dhx
 */
public interface IInternalOrderService {

    /**
     * 创建订单
     * 将订单信息保存到数据库，支持批量创建订单项
     *
     * @param userId  用户ID
     * @param request 订单创建请求，包含订单号和订单项列表
     */
    void createOrder(Long userId, CreateOrderRequest request);
}
