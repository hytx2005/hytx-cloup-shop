package com.chenzhihao.products.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 下单返回实体类
 * @author dhx
 */
@Data
public class ComPayVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String orderNo;
}
