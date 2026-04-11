package com.chenzhihao.products.domain.po;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 发送到kafka中的消息
 * @author dhx
 */
@Data
@Builder
public class ComKafka implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 商品id
     */
    private Long comId;

    /**
     * 购买数量
     */
    private Integer num;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
