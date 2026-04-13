package com.chenzhihao.orders.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chenzhihao.orders.domain.dto.OrderDelDto;
import com.chenzhihao.orders.domain.po.Orders;
import com.chenzhihao.orders.service.IOrdersService;
import com.chenzhihao.shopcommon.exception.BaseException;
import com.chenzhihao.shopcommon.result.Result;
import com.chenzhihao.shopcommon.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单模块Controller
 * 提供订单相关的REST API接口，包括订单查询和删除功能
 *
 * @author hqh
 * @since 2025-06-27
 */
@RestController
@RequestMapping("/orders")
public class OrdersController {

    private IOrdersService ordersService;
    @Autowired
    public void setOrdersService(IOrdersService ordersService) {
        this.ordersService = ordersService;
    }




    /**
     * 获取当前登录用户的订单列表
     * 根据用户ID查询该用户的所有订单信息
     *
     * @return 订单列表，包含当前用户的所有订单
     */
    @GetMapping("/get")
    public Result<List<Orders>> getOrdersByUserId() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BaseException("用户未登录");
        }
        List<Orders> orders = ordersService.list(new QueryWrapper<Orders>().eq("user_id", userId));
        return Result.success(orders);
    }


    /**
     * 根据订单ID列表删除订单
     * 只能删除当前登录用户的订单，每次只能删除一个订单
     *
     * @param dto 订单删除DTO，包含要删除的订单ID列表
     * @return 删除操作结果
     */
    @DeleteMapping("/delete")
    public Result<?> deleteOrder(@RequestBody OrderDelDto dto) {
        boolean b = ordersService.deleteOrders(dto);
        if (b) {
            return Result.success();
        }else {
            return Result.error("删除失败");
        }
    }
}
