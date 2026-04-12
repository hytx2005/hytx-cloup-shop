package com.chenzhihao.orders.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chenzhihao.api.client.CommodityClient;
import com.chenzhihao.orders.domain.po.Orders;
import com.chenzhihao.orders.mapper.OrdersMapper;
import com.chenzhihao.orders.service.IOrderTimeoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 订单超时处理服务实现类
 *
 * @author Claude
 */
@Service
public class OrderTimeoutServiceImpl implements IOrderTimeoutService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private CommodityClient commodityClient;

    @Override
    public void processTimeoutOrder(String orderNo) {
        try {
            // 查询订单当前状态
            QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_no", orderNo);

            Orders order = ordersMapper.selectOne(queryWrapper);

            if (order != null && "PENDING".equals(order.getPayStatus())) {
                // 订单仍然处于待支付状态，执行取消操作
                cancelPayment(orderNo);
                System.out.println("订单超时取消: " + orderNo);
            }
        } catch (Exception e) {
            // 处理异常，可以记录日志或发送告警
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public boolean cancelPayment(String orderNo) {
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        List<Orders> orders = ordersMapper.selectList(queryWrapper);

        if (orders.isEmpty()) {
            return false;
        }

        Orders order = orders.get(0);

        // 只有待支付状态的订单才能取消
        if (!"PENDING".equals(order.getPayStatus())) {
            return false;
        }

        // 更新订单状态为已取消
        UpdateWrapper<Orders> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_no", orderNo)
                .set("pay_status", "CANCELLED")
                .set("update_time", new Date());

        boolean updated = ordersMapper.update(null, updateWrapper) > 0;

        if (updated) {
            // 释放库存
            releaseInventory(orderNo);
        }

        return updated;
    }

    /**
     * 释放库存
     * @param orderNo 订单号
     */
    private void releaseInventory(String orderNo) {
        // 查询订单中的商品信息
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        List<Orders> orders = ordersMapper.selectList(queryWrapper);

        // 为每个商品释放库存
        for (Orders order : orders) {
            CommodityClient.ReleaseStockRequest request = new CommodityClient.ReleaseStockRequest();
            request.setCommodityId(order.getCommodityId());
            request.setQuantity(order.getCommodityNum());
            commodityClient.releaseStock(request);
        }
    }
}