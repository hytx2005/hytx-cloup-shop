package com.chenzhihao.orders.controller;


import com.chenzhihao.api.dto.CommodityDTO;
import com.chenzhihao.api.dto.OrderCreDto;
import com.chenzhihao.api.dto.OrderDetailDto;
import com.chenzhihao.api.facade.CommodityFacade;
import com.chenzhihao.api.vo.CommodityPayVo;
import com.chenzhihao.orders.domain.po.Orders;
import com.chenzhihao.orders.service.IOrdersService;
import com.chenzhihao.shopcommon.annotation.DubboServiceAop;
import com.chenzhihao.shopcommon.exception.BaseException;
import com.chenzhihao.shopcommon.result.Result;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单模块 前端控制器
 * @author hqh
 * @since 2025-06-27
 */
@RestController
@RequestMapping("/orders")
public class OrdersController {
    @Autowired
    private IOrdersService ordersService;




    /**
     * 生成订单号
     *    1.扣减商品数量
     *    2.生成订单表数据
     *    3.去购物车中删除对应数据
     * @param orderCreDto 订单信息
     * @return {@link Result }<{@link String }
     */
    @GetMapping("/pay")
    public Result<String> createOrder(@RequestBody OrderCreDto orderCreDto) {
        String order = ordersService.createOrder(orderCreDto);
        return Result.success(order);
    }

    @GetMapping("/test")
    public Result<?> getCommodity1() {
        throw new BaseException("测试异常");
    }

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
