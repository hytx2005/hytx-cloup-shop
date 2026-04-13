package com.chenzhihao.carts.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * 购物车DTO
 * 用于添加商品到购物车的请求参数封装
 *
 * @author hqh
 * @since 2025-07-03
 */
@Data
public class CartDTO {

    /**
     * 商品ID
     * 要添加到购物车的商品ID
     */
    private Long commodityId;

    /**
     * 商品名称
     */
    private String commodityName;

    /**
     * 商品价格
     * 单位为元
     */
    private BigDecimal commodityPrice;

    /**
     * 商品图片URL
     */
    private String commodityUrl;

    /**
     * 商品数量
     * 要添加的商品数量
     */
    private Integer commodityNum;

    /**
     * 商品规格
     */
    private String spec;

}


