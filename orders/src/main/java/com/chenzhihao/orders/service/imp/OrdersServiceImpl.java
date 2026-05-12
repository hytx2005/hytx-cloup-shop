package com.chenzhihao.orders.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chenzhihao.api.client.CartClient;
import com.chenzhihao.api.client.CommodityClient;
import com.chenzhihao.orders.domain.dto.OrderDelDto;
import com.chenzhihao.orders.domain.po.Orders;
import com.chenzhihao.orders.mapper.OrdersMapper;
import com.chenzhihao.orders.service.IOrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenzhihao.shopcommon.exception.BaseException;
import com.chenzhihao.shopcommon.util.OrderNoUtil;
import com.chenzhihao.shopcommon.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单模块Service实现类
 * 实现订单相关的业务逻辑，包括订单删除功能
 *
 * @author hqh
 * @since 2025-06-27
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

    @Autowired
    private CommodityClient commodityClient;
    @Autowired
    private CartClient cartClient;


    private OrdersMapper ordersMapper;
    @Autowired
    public void setOrdersMapper(OrdersMapper ordersMapper) {
        this.ordersMapper = ordersMapper;
    }

    /**
     * 根据订单ID集合删除订单
     * 删除条件：订单ID在指定集合中且订单属于当前登录用户
     *
     * @param dto 订单删除DTO，包含要删除的订单ID列表
     * @return 删除是否成功
     */
    @Override
    public boolean deleteOrders(OrderDelDto dto) {
        // 构造删除条件  id in IDs 并且 userId = 当前登录用户id
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", dto.getIds());
        queryWrapper.eq("user_id", UserContext.getUserId());
        int delete = ordersMapper.delete(queryWrapper);
        return delete > 0;
    }
}
