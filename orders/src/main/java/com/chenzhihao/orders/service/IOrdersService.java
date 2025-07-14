package com.chenzhihao.orders.service;

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
     * 根据订单id集合删除id
     * @param dto 订单信息集合
     * @return boolean
     */
    boolean deleteOrders(OrderDelDto dto);
}
