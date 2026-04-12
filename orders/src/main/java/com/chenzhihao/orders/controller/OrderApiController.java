package com.chenzhihao.orders.controller;

import com.chenzhihao.orders.domain.po.Orders;
import com.chenzhihao.orders.mapper.OrdersMapper;
import com.chenzhihao.shopcommon.exception.BaseException;
import com.chenzhihao.shopcommon.util.UserContext;
import com.chenzhihao.shopcommon.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Order API for inter-service communication (replaces Dubbo OrderFacade)
 * @author dhx
 */
@RestController
@RequestMapping("/api/internal/orders")
public class OrderApiController {

    private OrdersMapper ordersMapper;

    @Autowired
    public void setOrdersMapper(OrdersMapper ordersMapper) {
        this.ordersMapper = ordersMapper;
    }

    /**
     * Create order based on product information and order number
     * @param request CreateOrderRequest containing product info and order number
     * @return Result indicating success or failure
     */
    @PostMapping("/create")
    public Result<Boolean> createOrder(@RequestBody com.chenzhihao.api.client.OrderClient.CreateOrderRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BaseException("获取用户数据失败");
        }

        // Generate order data
        for (com.chenzhihao.api.client.OrderClient.OrderItem item : request.getOrderItems()) {
            BigDecimal money = item.getPrice().multiply(new BigDecimal(item.getNum()));
            Orders orders = Orders.builder()
                    .userId(userId)
                    .orderNo(request.getOrderNo())
                    .commodityId(item.getId())
                    .commodityName(item.getName())
                    .commodityNum(item.getNum())
                    .commodityUrl(item.getImageUrl())
                    .money(money)
                    .payStatus("未支付")
                    .build();
            ordersMapper.insert(orders);
        }

        return Result.success(true);
    }

    }