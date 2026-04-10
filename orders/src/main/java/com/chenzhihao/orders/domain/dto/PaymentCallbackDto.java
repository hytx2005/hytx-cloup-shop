package com.chenzhihao.orders.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付回调DTO
 *
 * @author Claude
 */
@Data
public class PaymentCallbackDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付状态：SUCCESS-成功，FAILED-失败
     */
    private String payStatus;

    /**
     * 支付交易号
     */
    private String tradeNo;

    /**
     * 支付时间（时间戳）
     */
    private Long payTime;

    /**
     * 签名（用于验证回调的合法性）
     */
    private String sign;
}