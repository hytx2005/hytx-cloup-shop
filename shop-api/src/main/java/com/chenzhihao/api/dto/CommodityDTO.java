package com.chenzhihao.api.dto;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 商品模块
 * </p>
 *
 * @author hqh
 * @since 2025-06-27
 */
@Data
@Builder
public class CommodityDTO implements Serializable {

    /**
     * 商品id
     */
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
     * 商品图片地址
     */
    private String imageUrl;

    /**
     * 已售数量
     */
    private Integer sold;

    /**
     * 商品规格
     */
    private String spec;


}
