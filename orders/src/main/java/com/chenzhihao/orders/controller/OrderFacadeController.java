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
 * Order Facade REST API (replaces Dubbo OrderFacadeImp)
 * @author dhx
 */
@RestController
@RequestMapping("/api/internal/orders")
public class OrderFacadeController {

    private OrdersMapper ordersMapper;

    @Autowired
    public void setOrdersMapper(OrdersMapper ordersMapper) {
        this.ordersMapper = ordersMapper;
    }

    /**
     * 根据商品信息和订单号生成预支付订单 (Dubbo createOrder equivalent)
     * @param pays    商品信息集合
     * @param orderNo 订单号
     */
    @PostMapping("/create")
    public Result<Void> createOrder(@RequestBody CreateOrderRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BaseException("获取用户数据失败");
        }

        // 2.生成订单表数据
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