package com.chenzhihao.carts.domain.po;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 购物车
 * </p>
 *
 * @author hqh
 * @since 2025-07-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("cart")
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 购物车id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 商品id
     */
    @TableField("commodity_id")
    private Long commodityId;

    /**
     * 商品名称
     */
    @TableField("commodity_name")
    private String commodityName;

    /**
     * 商品单价
     */
    @TableField("commodity_price")
    private BigDecimal commodityPrice;

    /**
     * 商品的url
     */
    @TableField("commodity_url")
    private String commodityUrl;

    /**
     * 商品数量
     */
    @TableField("commodity_num")
    private Integer commodityNum;

    /**
     * 商品规格
     */
    @TableField("spec")
    private String spec;


}
