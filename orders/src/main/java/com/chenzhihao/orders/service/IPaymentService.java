package com.chenzhihao.orders.service;

import com.chenzhihao.orders.domain.dto.PaymentCallbackDto;
import com.chenzhihao.orders.domain.dto.PaymentRequestDto;
import com.chenzhihao.orders.domain.vo.PaymentResultVo;

/**
 * 支付服务接口
 *
 * @author Claude
 */
public interface IPaymentService {

    /**
     * 创建支付请求
     *
     * @param paymentRequestDto 支付请求信息
     * @return 支付结果
     */
    PaymentResultVo createPayment(PaymentRequestDto paymentRequestDto);

    /**
     * 处理支付回调
     *
     * @param callbackDto 支付回调信息
     * @return 处理结果
     */
    boolean handlePaymentCallback(PaymentCallbackDto callbackDto);

    /**
     * 查询支付状态
     *
     * @param orderNo 订单号
     * @return 支付结果
     */
    PaymentResultVo queryPaymentStatus(String orderNo);

    /**
     * 取消支付（超时处理）
     *
     * @param orderNo 订单号
     * @return 是否成功
     */
    boolean cancelPayment(String orderNo);

    /**
     * 验证支付回调签名
     *
     * @param callbackDto 回调信息
     * @return 是否有效
     */
    boolean validateCallbackSignature(PaymentCallbackDto callbackDto);
}