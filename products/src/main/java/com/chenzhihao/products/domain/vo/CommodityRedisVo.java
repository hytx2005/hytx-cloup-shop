package com.chenzhihao.products.domain.vo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商品Redis缓存VO
 * 用于存储在Redis中的商品信息，包含商品基本属性和库存相关信息
 *
 * @author dhx
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommodityRedisVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 商品ID
     * 唯一标识一个商品
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

    /**
     * 库存数量
     * 当前可用库存
     */
    private Integer stock;

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

    /**
     * 状态
     * 0-下架，1-上架
     */
    private Integer status;

    /**
     * 上架用户ID
     * 上架该商品的用户ID
     */
    private Long userId;

    /**
     * 锁定库存
     * 等待支付结果到达后再处理的库存数量
     */
    private Integer payNum;

    /**
     * 版本号
     * 标记库存是否被修改过，用于乐观锁控制
     */
    private Integer version;
}
