package com.chenzhihao.carts.domain.vo;


import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车VO
 * 用于展示购物车商品信息的视图对象
 *
 * @author 虎哥
 * @since 2023-05-05
 */
@Data
public class CartVO {

    /**
     * 购物车ID
     * 购物车记录的唯一标识
     */
    private Long id;

    /**
     * 用户ID
     * 添加购物车的用户用户ID
     */
    private Long userId;

    /**
     * 商品ID
     * 购物车中商品的ID
     */
    private Long commodityId;

    /**
     * 商品名称
     */
    private String commodityName;

    /**
     * 商品单价
     * 单位为元
     */
    private BigDecimal commodityPrice;

    /**
     * 商品图片URL
     */
    private String commodityUrl;

    /**
     * 商品数量
     * 购物车中该商品的数量
     */
    private Integer commodityNum;

    /**
     * 商品规格
     */
    private String spec;

}
