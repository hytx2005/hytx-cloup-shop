package com.chenzhihao.products.domain.vo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommodityRedisVo implements Serializable {
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

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 已售数量
     */
    private Integer sold;

    /**
     * 商品规格
     */
    private String spec;

    /**
     * 状态（0/1）
     */
    private Integer status;

    /**
     * 上架该商品的用户id
     */
    private Long userId;


    /**
     * 锁定库存，等待支付结果到达再处理
     */
    private Integer payNum;


    /**
     * 标记库存是否被修改过
     */
    private Integer version;
}
