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

    private Long id;

    private String name;

    private BigDecimal price;

    private String imageUrl;

    private Integer sold;

    private String spec;


}
