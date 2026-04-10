package com.chenzhihao.orders.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付请求DTO
 *
 * @author Claude
 */
@Data
public class PaymentRequestDto implements Serializable {

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
     * 支付方式：ALIPAY-支付宝，WECHAT-微信，SIMULATE-模拟支付
     */
    private String payMethod;

    /**
     * 支付描述
     */
    private String description;
}