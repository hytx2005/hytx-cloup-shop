package com.chenzhihao.api.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单支付商品信息DTO
 * 根据提供的商品信息生成预支付订单
 *
 * @author dhx
 */
@Data
@Builder
public class OrderForPay implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     * 商品的唯一标识
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品价格
     * 单位为元
     */
    private BigDecimal price;

    /**
     * 商品图片链接
     */
    private String imageUrl;

    /**
     * 商品数量
     * 购买的商品数量
     */
    private Integer num;
}
