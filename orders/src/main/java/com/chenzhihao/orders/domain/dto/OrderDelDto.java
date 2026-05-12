package com.chenzhihao.orders.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 订单删除DTO
 * 用于批量删除订单的请求参数
 *
 * @author ASUS
 */
@Data
public class OrderDelDto {
    private List<Long> ids;
}
