package com.chenzhihao.orders.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chenzhihao.orders.domain.dto.OrderDelDto;
import com.chenzhihao.orders.domain.po.Orders;
import com.chenzhihao.orders.service.IOrdersService;
import com.chenzhihao.shopcommon.annotation.DubboException;
import com.chenzhihao.shopcommon.exception.BaseException;
import com.chenzhihao.shopcommon.result.Result;
import com.chenzhihao.shopcommon.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单模块 前端控制器
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
     * 获取用户订单信息
     * @return {@link Result }<{@link List }<{@link Orders }>>
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
     * 根据id删除订单信息
     * @param dto 订单id列表
     * @return {@link Result }<{@link ? }>
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
