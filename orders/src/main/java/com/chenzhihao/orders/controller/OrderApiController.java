package com.chenzhihao.orders.controller;

import com.chenzhihao.api.client.OrderClient.CreateOrderRequest;
import com.chenzhihao.orders.service.IInternalOrderService;
import com.chenzhihao.shopcommon.exception.BaseException;
import com.chenzhihao.shopcommon.util.UserContext;
import com.chenzhihao.shopcommon.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Order API for inter-service communication (replaces Dubbo OrderFacade)
 * @author dhx
 */
@RestController
@RequestMapping("/api/internal/orders")
public class OrderApiController {

    @Autowired
    private IInternalOrderService internalOrderService;

    /**
     * Create order based on product information and order number
     * @param request CreateOrderRequest containing product info and order number
     * @return Result indicating success or failure
     */
    @PostMapping("/create")
    public Result<Boolean> createOrder(@RequestBody CreateOrderRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BaseException("获取用户数据失败");
        }

        internalOrderService.createOrder(userId, request);
        return Result.success(true);
    }

}