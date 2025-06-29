package com.chenzhihao.orders.controller;


import com.chenzhihao.orders.domain.po.Orders;
import com.chenzhihao.orders.service.IOrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 订单模块 前端控制器
 * </p>
 *
 * @author hqh
 * @since 2025-06-27
 */
@RestController
@RequestMapping("/orders")
public class OrdersController {
    @Autowired
    private IOrdersService ordersService;

    /**
     * 添加订单
     * @param orders 订单对象
     * @return 添加结果
     */
    @PostMapping
    public boolean addOrder(@RequestBody Orders orders) {
        return ordersService.save(orders);
    }

    /**
     * 删除订单
     * @param id 订单ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public boolean deleteOrder(@PathVariable Long id) {
        return ordersService.removeById(id);
    }

    /**
     * 修改订单
     * @param orders 订单对象
     * @return 修改结果
     */
    @PutMapping
    public boolean updateOrder(@RequestBody Orders orders) {
        return ordersService.updateById(orders);
    }

    /**
     * 查询订单列表
     * @return 订单列表
     */
    @GetMapping
    public List<Orders> getOrders() {
        return ordersService.list();
    }

    /**
     * 根据ID查询订单
     * @param id 订单ID
     * @return 订单对象
     */
    @GetMapping("/{id}")
    public Orders getOrderById(@PathVariable Long id) {
        return ordersService.getById(id);
    }

}
