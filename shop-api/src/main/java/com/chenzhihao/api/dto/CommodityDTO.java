package com.chenzhihao.api.dto;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品DTO
 * 用于服务间传递商品信息的DTO对象
 *
 * @author hqh
 * @since 2025-06-27
 */
@Data
@Builder
public class CommodityDTO implements Serializable {

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
     * 商品图片地址
     */
    private String imageUrl;

    /**
     * 已售数量
     * 商品累计销量
     */
    private Integer sold;

    /**
     * 商品规格
     * 商品的规格描述
     */
    private String spec;

}
