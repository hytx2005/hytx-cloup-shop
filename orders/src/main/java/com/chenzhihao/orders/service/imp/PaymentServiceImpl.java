package com.chenzhihao.orders.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chenzhihao.api.facade.CommodityFacade;
import com.chenzhihao.orders.domain.dto.PaymentCallbackDto;
import com.chenzhihao.orders.domain.dto.PaymentRequestDto;
import com.chenzhihao.orders.domain.po.Orders;
import com.chenzhihao.orders.domain.vo.PaymentResultVo;
import com.chenzhihao.orders.mapper.OrdersMapper;
import com.chenzhihao.orders.service.IPaymentService;
import com.chenzhihao.shopcommon.exception.BaseException;
import com.chenzhihao.shopcommon.util.OrderNoUtil;
import com.chenzhihao.orders.util.DelayedQueueUtil;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 支付服务实现类
 *
 * @author Claude
 */
@Service
public class PaymentServiceImpl implements IPaymentService {

    @Autowired
    private OrdersMapper ordersMapper;

    @DubboReference
    private CommodityFacade commodityFacade;

    @Autowired
    private DelayedQueueUtil delayedQueueUtil;

    private static final String SIGN_KEY = "payment_secret_key_2024";

    @Override
    public PaymentResultVo createPayment(PaymentRequestDto paymentRequestDto) {
        // 查询订单信息
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", paymentRequestDto.getOrderNo());
        List<Orders> orders = ordersMapper.selectList(queryWrapper);

        if (orders.isEmpty()) {
            throw new BaseException("订单不存在");
        }

        Orders order = orders.get(0);

        // 检查订单状态
        if (!"PENDING".equals(order.getPayStatus())) {
            throw new BaseException("订单状态不正确，无法支付");
        }

        // 生成支付交易号
        String tradeNo = "PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);

        // 创建支付结果
        PaymentResultVo result = new PaymentResultVo();
        result.setOrderNo(paymentRequestDto.getOrderNo());
        result.setAmount(paymentRequestDto.getAmount());
        result.setTradeNo(tradeNo);

        if ("SIMULATE".equals(paymentRequestDto.getPayMethod())) {
            // 模拟支付直接成功
            result.setStatus("SUCCESS");
            result.setPayTime(System.currentTimeMillis());
            result.setPayUrl("/api/orders/payment/simulate?orderNo=" + paymentRequestDto.getOrderNo());
        } else {
            // 其他支付方式跳转到支付页面
            result.setStatus("PENDING");
            result.setPayUrl("/api/orders/payment/page?orderNo=" + paymentRequestDto.getOrderNo() +
                    "&amount=" + paymentRequestDto.getAmount() +
                    "&method=" + paymentRequestDto.getPayMethod());
        }

        return result;
    }

    @Override
    @Transactional
    public boolean handlePaymentCallback(PaymentCallbackDto callbackDto) {
        // 验证签名
        if (!validateCallbackSignature(callbackDto)) {
            throw new BaseException("回调签名验证失败");
        }

        // 查询订单
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", callbackDto.getOrderNo());
        List<Orders> orders = ordersMapper.selectList(queryWrapper);

        if (orders.isEmpty()) {
            throw new BaseException("订单不存在");
        }

        Orders order = orders.get(0);

        // 更新订单状态
        UpdateWrapper<Orders> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_no", callbackDto.getOrderNo());

        boolean updated = false;
        if ("SUCCESS".equals(callbackDto.getPayStatus())) {
            updateWrapper.set("pay_status", "PAID")
                    .set("pay_time", new Date())
                    .set("trade_no", callbackDto.getTradeNo());

            updated = ordersMapper.update(null, updateWrapper) > 0;

            if (updated) {
                // 支付成功，从延迟队列中移除订单
                delayedQueueUtil.removeOrderFromTimeoutQueue(callbackDto.getOrderNo());
            }
        } else if ("FAILED".equals(callbackDto.getPayStatus())) {
            updateWrapper.set("pay_status", "FAILED");
            updated = ordersMapper.update(null, updateWrapper) > 0;

            if (updated) {
                // 支付失败，释放库存
                releaseInventory(callbackDto.getOrderNo());
            }
        }

        return updated;
    }

    @Override
    public PaymentResultVo queryPaymentStatus(String orderNo) {
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        List<Orders> orders = ordersMapper.selectList(queryWrapper);

        if (orders.isEmpty()) {
            throw new BaseException("订单不存在");
        }

        Orders order = orders.get(0);
        PaymentResultVo result = new PaymentResultVo();
        result.setOrderNo(orderNo);
        result.setAmount(order.getMoney());
        result.setTradeNo(order.getTradeNo());

        switch (order.getPayStatus()) {
            case "PAID":
                result.setStatus("SUCCESS");
                result.setPayTime(order.getPayTime() != null ? order.getPayTime().getTime() : null);
                break;
            case "PENDING":
                result.setStatus("PENDING");
                break;
            case "CANCELLED":
                result.setStatus("CANCELLED");
                break;
            case "FAILED":
                result.setStatus("FAILED");
                break;
            default:
                result.setStatus("UNKNOWN");
        }

        return result;
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

    @Override
    public boolean validateCallbackSignature(PaymentCallbackDto callbackDto) {
        // 简单的签名验证逻辑
        String expectedSign = generateSignature(callbackDto);
        return expectedSign.equals(callbackDto.getSign());
    }

    /**
     * 释放库存
     */
    private void releaseInventory(String orderNo) {
        // 查询订单中的商品信息
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        List<Orders> orders = ordersMapper.selectList(queryWrapper);

        // 为每个商品释放库存
        for (Orders order : orders) {
            commodityFacade.releaseStock(order.getCommodityId(), order.getCommodityNum());
        }
    }

    /**
     * 生成签名
     */
    private String generateSignature(PaymentCallbackDto callbackDto) {
        // 简单的签名生成逻辑，实际项目中应该使用更复杂的加密算法
        String content = callbackDto.getOrderNo() + callbackDto.getAmount() + callbackDto.getPayStatus() + SIGN_KEY;
        return Integer.toHexString(content.hashCode());
    }
}