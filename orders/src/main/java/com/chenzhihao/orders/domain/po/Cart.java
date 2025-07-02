package com.chenzhihao.orders.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.dubbo.common.logger.FluentLogger;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物车表
 * @author dhx
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("cart")
public class Cart implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

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
     * 单个商品价格
     */
    @TableField("commodity_price")
    private BigDecimal commodityPrice;
    /**
     * 商品名称
     */
    @TableField("commodity_name")
    private String commodityName;
    /**
     * 商品图片
     */
    @TableField("commodity_url")
    private String commodityUrl;
    /**
     * 商品数量
     */
    @TableField("commodity_num")
    private Integer commodityNum;
    /**
     * 总价
     */
    @TableField("total_price")
    private BigDecimal totalPrice;
}
