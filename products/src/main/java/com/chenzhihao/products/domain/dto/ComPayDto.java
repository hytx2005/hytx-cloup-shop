package com.chenzhihao.products.domain.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 下单请求参数
 * @author dhx
 */
@Data
public class ComPayDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<PayDetail> payDetails;
}
