package com.chenzhihao.api.vo;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

/**
 * 商品支付结果VO
 * 下单后返回的商品信息
 *
 * @author dhx
 */
@Data
@Builder
public class CommodityPayVo implements Serializable {
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
}
