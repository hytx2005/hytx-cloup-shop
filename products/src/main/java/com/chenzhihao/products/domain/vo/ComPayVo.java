package com.chenzhihao.products.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 商品支付结果VO
 * 用于返回商品支付后的结果数据
 *
 * @author dhx
 */
@Data
public class ComPayVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String orderNo;
}
