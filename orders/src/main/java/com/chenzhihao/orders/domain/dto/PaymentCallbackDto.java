package com.chenzhihao.orders.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 支付回调DTO
 * 用于接收支付平台回调的支付结果数据
 *
 * @author Claude
 */
@Data
public class PaymentCallbackDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单号
     * 唯一标识一个订单
     */
    private String orderNo;

    /**
     * 支付金额
     * 实际支付金额，单位为元
     */
    private BigDecimal amount;

    /**
     * 支付状态
     * 可选值：SUCCESS-成功，FAILED-失败
     */
    private String payStatus;

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
     * 签名
     * 用于验证回调请求的合法性，防止伪造回调
     */
    private String sign;
}