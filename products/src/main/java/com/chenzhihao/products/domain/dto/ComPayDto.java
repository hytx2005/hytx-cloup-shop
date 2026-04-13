package com.chenzhihao.products.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 商品支付请求DTO
 * 用于发起商品支付的请求参数封装
 *
 * @author dhx
 */
@Data
public class ComPayDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<PayDetail> payDetails;
}
