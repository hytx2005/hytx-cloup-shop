package com.chenzhihao.carts.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;


    /**
     * <p>
     * 购物车
     * </p>
     *
     * @author hqh
     * @since 2025-07-03
     */
    @Data

    public class CartDTO {

        private Long commodityId;

        private String commodityName;

        private BigDecimal commodityPrice;

        private String commodityUrl;

        private Integer commodityNum;

        private String spec;


    }


