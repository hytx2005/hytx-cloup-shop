package com.chenzhihao.orders.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付请求DTO
 * 用于发起支付请求的参数封装
 *
 * @author Claude
 */
@Data
public class PaymentRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     * 唯一标识一个订单
     */
    private String orderNo;

    /**
     * 支付金额
     * 订单总金额，单位为元
     */
    private BigDecimal amount;

    /**
     * 支付方式
     * 可选值：ALIPAY-支付宝，WECHAT-微信，SIMULATE-模拟支付
     */
    private String payMethod;

    /**
     * 支付描述
     * 支付订单的描述信息
     */
    private String description;
}