package com.chenzhihao.orders.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chenzhihao.api.client.CommodityClient;
import com.chenzhihao.orders.domain.dto.PaymentCallbackDto;
import com.chenzhihao.orders.domain.dto.PaymentRequestDto;
import com.chenzhihao.orders.domain.po.Orders;
import com.chenzhihao.orders.domain.vo.PaymentResultVo;
import com.chenzhihao.orders.mapper.OrdersMapper;
import com.chenzhihao.orders.service.IPaymentService;
import com.chenzhihao.shopcommon.exception.BaseException;
import com.chenzhihao.shopcommon.util.OrderNoUtil;
import com.chenzhihao.orders.service.IOrderTimeoutService;
import com.chenzhihao.orders.util.PaymentQueueManager;
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
 * 处理支付相关的业务逻辑，包括创建支付、处理回调、查询状态等
 *
 * @author Claude
 */
@Service
public class PaymentServiceImpl implements IPaymentService {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private CommodityClient commodityClient;

    @Autowired
    private IOrderTimeoutService orderTimeoutService;

    @Autowired
    private PaymentQueueManager paymentQueueManager;

    /**
     * 签名密钥，实际项目中应该配置在配置文件中
     */
    private static final String SIGN_KEY = "payment_secret_key_2024";

    @Override
    public PaymentResultVo createPayment(PaymentRequestDto paymentRequestDto) {
        // 1. 查询订单信息
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", paymentRequestDto.getOrderNo());
        List<Orders> orders = ordersMapper.selectList(queryWrapper);

        if (orders.isEmpty()) {
            throw new BaseException("订单不存在");
        }

        Orders order = orders.get(0);

        // 2. 检查订单状态
        if (!"PENDING".equals(order.getPayStatus())) {
            throw new BaseException("订单状态不正确，无法支付");
        }

        // 3. 生成支付交易号
        String tradeNo = "PAY" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);

        // 4. 创建支付结果
        PaymentResultVo result = new PaymentResultVo();
        result.setOrderNo(paymentRequestDto.getOrderNo());
        result.setAmount(paymentRequestDto.getAmount());
        result.setTradeNo(tradeNo);

        if ("SIMULATE".equals(paymentRequestDto.getPayMethod())) {
            // 5.1 模拟支付直接成功
            result.setStatus("SUCCESS");
            result.setPayTime(System.currentTimeMillis());
            result.setPayUrl("/api/orders/payment/simulate?orderNo=" + paymentRequestDto.getOrderNo());
        } else {
            // 5.2 其他支付方式跳转到支付页面
            result.setStatus("PENDING");
            result.setPayUrl("/api/orders/payment/page?orderNo=" + paymentRequestDto.getOrderNo() +
                    "&amount=" + paymentRequestDto.getAmount() +
                    "&method=" + paymentRequestDto.getPayMethod());

            // 6. 将订单添加到延迟队列（15分钟后超时）
            paymentQueueManager.addOrderToTimeoutQueue(paymentRequestDto.getOrderNo());
        }

        return result;
    }

    @Override
    @Transactional
    public boolean handlePaymentCallback(PaymentCallbackDto callbackDto) {
        // 1. 验证签名
        if (!validateCallbackSignature(callbackDto)) {
            throw new BaseException("回调签名验证失败");
        }

        // 2. 查询订单
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", callbackDto.getOrderNo());
        List<Orders> orders = ordersMapper.selectList(queryWrapper);

        if (orders.isEmpty()) {
            throw new BaseException("订单不存在");
        }

        Orders order = orders.get(0);

        // 3. 更新订单状态
        UpdateWrapper<Orders> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_no", callbackDto.getOrderNo());

        boolean updated = false;
        if ("SUCCESS".equals(callbackDto.getPayStatus())) {
            // 3.1 支付成功，更新订单状态为已支付
            updateWrapper.set("pay_status", "PAID")
                    .set("pay_time", new Date())
                    .set("trade_no", callbackDto.getTradeNo());

            updated = ordersMapper.update(null, updateWrapper) > 0;

            if (updated) {
                // 3.2 支付成功，从延迟队列中移除订单
                paymentQueueManager.removeOrderFromTimeoutQueue(callbackDto.getOrderNo());
            }
        } else if ("FAILED".equals(callbackDto.getPayStatus())) {
            // 3.3 支付失败，更新订单状态为失败
            updateWrapper.set("pay_status", "FAILED");
            updated = ordersMapper.update(null, updateWrapper) > 0;

            if (updated) {
                // 3.4 支付失败，释放库存（由订单超时服务处理）
                // This is now handled by the order timeout service when needed
            }
        }

        return updated;
    }

    @Override
    public PaymentResultVo queryPaymentStatus(String orderNo) {
        // 1. 查询订单
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

        // 2. 映射订单状态到支付状态
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
    public boolean validateCallbackSignature(PaymentCallbackDto callbackDto) {
        // 简单的签名验证逻辑
        String expectedSign = generateSignature(callbackDto);
        return expectedSign.equals(callbackDto.getSign());
    }

    /**
     * 生成签名
     *
     * 根据订单号、金额、支付状态和密钥生成签名
     * 实际项目中应该使用更复杂的加密算法（如MD5、SHA256等）
     *
     * @param callbackDto 支付回调数据
     * @return 生成的签名
     */
    private String generateSignature(PaymentCallbackDto callbackDto) {
        // 简单的签名生成逻辑，实际项目中应该使用更复杂的加密算法
        String content = callbackDto.getOrderNo() + callbackDto.getAmount() + callbackDto.getPayStatus() + SIGN_KEY;
        return Integer.toHexString(content.hashCode());
    }
}