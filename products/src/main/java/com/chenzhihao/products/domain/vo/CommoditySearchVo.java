package com.chenzhihao.products.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品搜索结果VO - 替代CommodityEsDoc
 */
@Data
public class CommoditySearchVo {
    /**
     * 商品ID
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
     * 商品图片URL
     */
    private String imageUrl;

    /**
     * 商品规格
     */
    private String spec;

    /**
     * 销量
     */
    private Integer sold;

}