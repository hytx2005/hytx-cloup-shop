package com.chenzhihao.products.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author dhx
 */
@Data
@NoArgsConstructor
public class PayDetail implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 商品id
     */
    private Long commodityId;

    /**
     * 购买数量
     */
    private Integer num;


}
