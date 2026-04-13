package com.chenzhihao.orders.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付结果VO
 * 用于返回支付操作的结果数据
 *
 * @author Claude
 */
@Data
public class PaymentResultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     * 支付的订单号
     */
    private String orderNo;

    /**
     * 支付状态
     * 可选值：SUCCESS-成功，FAILED-失败，PENDING-处理中
     */
    private String status;

    /**
     * 支付金额
     * 实际支付金额，单位为元
     */
    private BigDecimal amount;

    /**
     * 支付交易号
     * 支付平台返回的交易流水号
     */
    private String tradeNo;

    /**
     * 支付时间
     * 支付完成的时间戳（毫秒）
     */
    private Long payTime;

    /**
     * 错误信息
     * 支付失败时的错误描述
     */
    private String errorMsg;

    /**
     * 支付跳转URL
     * 用于跳转到支付页面的URL（模拟支付时使用）
     */
    private String payUrl;
}