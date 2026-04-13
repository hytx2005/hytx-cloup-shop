package com.chenzhihao.products.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 支付详情DTO
 * 用于描述单个商品的购买详情
 *
 * @author dhx
 */
@Data
@NoArgsConstructor
public class PayDetail implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     * 要购买的商品ID
     */
    private Long commodityId;

    /**
     * 购买数量
     * 商品的购买数量
     */
    private Integer num;


}
