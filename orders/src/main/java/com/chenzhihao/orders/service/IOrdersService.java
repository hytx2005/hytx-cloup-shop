package com.chenzhihao.orders.service;

import com.chenzhihao.orders.domain.dto.OrderDelDto;
import com.chenzhihao.orders.domain.po.Orders;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 订单模块Service接口
 * 提供订单相关的业务逻辑处理，继承MyIService以获得基础CRUD功能
 *
 * @author hqh
 * @since 2025-06-27
 */
public interface IOrdersService extends IService<Orders> {




    /**
     * 根据订单ID集合删除订单
     * 只能删除当前登录用户的订单
     *
     * @param dto 订单删除DTO，包含要删除的订单ID列表
     * @return 删除是否成功
     */
    boolean deleteOrders(OrderDelDto dto);
}
