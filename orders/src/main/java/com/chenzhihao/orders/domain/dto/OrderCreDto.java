package com.chenzhihao.orders.domain.dto;

import java.util.List;

/**
 * 生成订单号请求参数
 * @author dhx
 */
public class OrderCreDto {

    /**
     * 商品参数列表
     */
    private List<OrderDetailDto> details;
}
