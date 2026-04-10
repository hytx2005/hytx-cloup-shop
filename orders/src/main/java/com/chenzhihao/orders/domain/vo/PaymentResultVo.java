package com.chenzhihao.orders.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付结果VO
 *
 * @author Claude
 */
@Data
public class PaymentResultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付状态：SUCCESS-成功，FAILED-失败，PENDING-处理中
     */
    private String status;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付交易号
     */
    private String tradeNo;

    /**
     * 支付时间
     */
    private Long payTime;

    /**
     * 错误信息（支付失败时）
     */
    private String errorMsg;

    /**
     * 支付跳转URL（用于模拟支付页面）
     */
    private String payUrl;
}