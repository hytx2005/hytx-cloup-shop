package com.chenzhihao.api.vo;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

/**
 * 下单后返回商品信息
 *
 * @author hqh
 * @since 2025-06-27
 */
@Data
@Builder
public class CommodityPayVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品图片链接
     */
    private String imageUrl;
}
