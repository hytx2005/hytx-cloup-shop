package com.chenzhihao.carts.domain.vo;


import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * 订单详情表
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-05
 */
@Data
public class CartVO {

    /**
     * 购物车id
     */
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 商品id
     */
    private Long commodityId;

    /**
     * 商品名称
     */
    private String commodityName;

    /**
     * 同一商品总价
     */
    private BigDecimal commodityPrice;

    /**
     * 商品的url
     */
    private String commodityUrl;

    /**
     * 商品数量
     */
    private Integer commodityNum;

    /**
     * 商品规格
     */
    private String spec;

}
