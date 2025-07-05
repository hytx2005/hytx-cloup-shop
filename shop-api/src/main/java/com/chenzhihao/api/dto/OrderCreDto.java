package com.chenzhihao.api.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 生成订单号请求参数
 * @author dhx
 */
@Data
public class OrderCreDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 商品参数列表
     */
    private List<OrderDetailDto> details;
}
