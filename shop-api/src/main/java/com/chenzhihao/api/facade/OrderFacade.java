package com.chenzhihao.api.facade;

import com.chenzhihao.api.dto.OrderForPay;

import java.util.List;

/**
 * 订单模块提供的dubbo服务
 * @author dhx
 */
public interface OrderFacade {

    /**
     * 根据商品信息生成预支付订单
     *
     * @param pays    商品信息集合
     * @param orderNo 订单号
     */
    void createOrder(List<OrderForPay> pays,String orderNo);
}
