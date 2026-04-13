package com.chenzhihao.orders.service;

import com.chenzhihao.orders.domain.po.Orders;

/**
 * 订单超时处理服务接口
 * 提供订单超时自动取消和支付取消功能
 *
 * @author Claude
 */
public interface IOrderTimeoutService {

    /**
     * 处理超时订单
     * 检查订单状态，如果订单仍处于待支付状态，则取消支付
     *
     * @param orderNo 订单号
     */
    void processTimeoutOrder(String orderNo);

    /**
     * 取消订单支付
     * 将订单状态从待支付改为已取消，并释放商品库存
     *
     * @param orderNo 订单号
     * @return 取消是否成功
     */
    boolean cancelPayment(String orderNo);
}