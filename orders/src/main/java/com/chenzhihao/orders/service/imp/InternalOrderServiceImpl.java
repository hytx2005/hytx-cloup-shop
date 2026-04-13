package com.chenzhihao.orders.service.imp;

import com.chenzhihao.api.client.OrderClient.CreateOrderRequest;
import com.chenzhihao.api.client.OrderClient.OrderItem;
import com.chenzhihao.orders.domain.po.Orders;
import com.chenzhihao.orders.mapper.OrdersMapper;
import com.chenzhihao.orders.service.IInternalOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 内部订单服务实现类
 *
 * @author dhx
 */
@Slf4j
@Service
public class InternalOrderServiceImpl implements IInternalOrderService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createOrder(Long userId, CreateOrderRequest request) {
        log.info("创建订单，用户ID: {}, 订单号: {}", userId, request.getOrderNo());

        for (OrderItem item : request.getOrderItems()) {
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
                    .createTime(new java.util.Date())
                    .updateTime(new java.util.Date())
                    .build();
            ordersMapper.insert(orders);
        }

        log.info("订单创建成功，订单号: {}", request.getOrderNo());
    }
}
