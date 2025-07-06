package com.chenzhihao.orders.service;

import com.chenzhihao.api.dto.OrderCreDto;
import com.chenzhihao.orders.domain.dto.OrderDelDto;
import com.chenzhihao.orders.domain.po.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 订单模块 服务类
 * </p>
 *
 * @author hqh
 * @since 2025-06-27
 */
public interface IOrdersService extends IService<Orders> {

    /**
     * 生成订单号
     *  1.扣减商品数量
     *  2.生成订单表数据
     *  3.去购物车中删除对应数据
     * @param orderCreDto 订单信息
     * @return {@link String }
     */
    String createOrder(OrderCreDto orderCreDto);


    /**
     * 根据订单id集合删除id
     * @param dto 订单信息集合
     * @return boolean
     */
    boolean deleteOrders(OrderDelDto dto);
}
