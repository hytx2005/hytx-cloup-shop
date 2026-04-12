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
 * 订单服务REST API接口
 * 替代原有的Dubbo OrderFacadeImp实现
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
     * 根据商品信息和订单号生成预支付订单
     * 替代原有的Dubbo createOrder方法
     * @param request 创建订单的请求对象
     * @return 创建订单操作结果
     */
    @PostMapping("/create")
    public Result<Void> createOrder(@RequestBody com.chenzhihao.api.client.OrderClient.CreateOrderRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BaseException("获取用户数据失败");
        }

        // 2.生成订单表数据
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

        return Result.success();
    }

}