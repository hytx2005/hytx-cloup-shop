package com.chenzhihao.api.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 单个订单详情表
 * @author dhx
 */
@Data
public class OrderDetailDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 商品id
     */
    private Long commodityId;

    /**
     * 商品数量
     */
    private Integer num;
}
