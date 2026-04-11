package com.chenzhihao.orders.controller;

import com.chenzhihao.api.dto.OrderForPay;
import com.chenzhihao.orders.domain.po.Orders;
import com.chenzhihao.orders.mapper.OrdersMapper;
import com.chenzhihao.shopcommon.exception.BaseException;
import com.chenzhihao.shopcommon.util.UserContext;
import com.chenzhihao.shopcommon.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

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
    public Result<Void> createOrder(@RequestBody CreateOrderRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BaseException("获取用户数据失败");
        }

        // Generate order data
        for (OrderForPay vo : request.getPays()) {
            BigDecimal money = vo.getPrice().multiply(new BigDecimal(vo.getNum()));
            Orders orders = Orders.builder()
                    .userId(userId)
                    .orderNo(request.getOrderNo())
                    .commodityId(vo.getId())
                    .commodityName(vo.getName())
                    .commodityNum(vo.getNum())
                    .commodityUrl(vo.getImageUrl())
                    .money(money)
                    .payStatus("未支付")
                    .build();
            ordersMapper.insert(orders);
        }

        return Result.success();
    }

    /**
     * Request DTO for creating orders
     */
    public static class CreateOrderRequest {
        private List<OrderForPay> pays;
        private String orderNo;

        // Getters and setters
        public List<OrderForPay> getPays() {
            return pays;
        }

        public void setPays(List<OrderForPay> pays) {
            this.pays = pays;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }
    }
}