package com.chenzhihao.orders.service;

import com.chenzhihao.orders.domain.po.Orders;

/**
 * 订单超时处理服务接口
 *
 * @author Claude
 */
public interface IOrderTimeoutService {

    /**
     * 处理超时订单
     *
     * @param orderNo 订单号
     */
    void processTimeoutOrder(String orderNo);

    /**
     * 取消订单支付
     *
     * @param orderNo 订单号
     * @return 是否成功
     */
    boolean cancelPayment(String orderNo);
}