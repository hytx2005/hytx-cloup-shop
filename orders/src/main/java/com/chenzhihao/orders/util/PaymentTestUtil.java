package com.chenzhihao.orders.util;

import com.chenzhihao.orders.domain.dto.PaymentCallbackDto;
import com.chenzhihao.orders.domain.dto.PaymentRequestDto;
import com.chenzhihao.orders.domain.vo.PaymentResultVo;
import com.chenzhihao.orders.service.IPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 支付测试工具类
 * 用于本地测试支付功能
 *
 * @author Claude
 */
@Component
public class PaymentTestUtil {

    @Autowired
    private IPaymentService paymentService;

    /**
     * 创建模拟支付请求
     *
     * @param orderNo 订单号
     * @param amount 支付金额
     * @return 支付结果
     */
    public PaymentResultVo createTestPayment(String orderNo, BigDecimal amount) {
        PaymentRequestDto requestDto = new PaymentRequestDto();
        requestDto.setOrderNo(orderNo);
        requestDto.setAmount(amount);
        requestDto.setPayMethod("SIMULATE");
        requestDto.setDescription("测试支付");

        return paymentService.createPayment(requestDto);
    }

    /**
     * 执行模拟支付回调
     *
     * @param orderNo 订单号
     * @param amount 支付金额
     * @param success 是否支付成功
     * @return 处理结果
     */
    public boolean executeTestCallback(String orderNo, BigDecimal amount, boolean success) {
        PaymentCallbackDto callbackDto = new PaymentCallbackDto();
        callbackDto.setOrderNo(orderNo);
        callbackDto.setAmount(amount);
        callbackDto.setPayStatus(success ? "SUCCESS" : "FAILED");
        callbackDto.setPayTime(System.currentTimeMillis());
        callbackDto.setTradeNo("TEST" + System.currentTimeMillis());

        // 生成测试签名
        String sign = Integer.toHexString(
                (orderNo + amount + callbackDto.getPayStatus() + "payment_secret_key_2024").hashCode()
        );
        callbackDto.setSign(sign);

        return paymentService.handlePaymentCallback(callbackDto);
    }

    /**
     * 测试完整的支付流程
     *
     * @param orderNo 订单号
     * @param amount 支付金额
     */
    public void testCompletePaymentFlow(String orderNo, BigDecimal amount) {
        System.out.println("=== 开始测试支付流程 ===");
        System.out.println("订单号: " + orderNo);
        System.out.println("支付金额: " + amount);

        // 1. 创建支付请求
        System.out.println("\n1. 创建支付请求...");
        PaymentResultVo paymentResult = createTestPayment(orderNo, amount);
        System.out.println("支付结果: " + paymentResult.getStatus());
        System.out.println("交易号: " + paymentResult.getTradeNo());

        // 2. 执行支付回调
        System.out.println("\n2. 执行支付回调...");
        boolean callbackResult = executeTestCallback(orderNo, amount, true);
        System.out.println("回调处理结果: " + (callbackResult ? "成功" : "失败"));

        // 3. 查询支付状态
        System.out.println("\n3. 查询支付状态...");
        PaymentResultVo statusResult = paymentService.queryPaymentStatus(orderNo);
        System.out.println("最终支付状态: " + statusResult.getStatus());

        System.out.println("\n=== 支付流程测试完成 ===");
    }

    /**
     * 测试支付失败流程
     *
     * @param orderNo 订单号
     * @param amount 支付金额
     */
    public void testPaymentFailureFlow(String orderNo, BigDecimal amount) {
        System.out.println("=== 开始测试支付失败流程 ===");
        System.out.println("订单号: " + orderNo);
        System.out.println("支付金额: " + amount);

        // 1. 创建支付请求
        System.out.println("\n1. 创建支付请求...");
        PaymentResultVo paymentResult = createTestPayment(orderNo, amount);
        System.out.println("支付结果: " + paymentResult.getStatus());

        // 2. 执行失败的支付回调
        System.out.println("\n2. 执行失败的支付回调...");
        boolean callbackResult = executeTestCallback(orderNo, amount, false);
        System.out.println("回调处理结果: " + (callbackResult ? "成功" : "失败"));

        // 3. 查询支付状态
        System.out.println("\n3. 查询支付状态...");
        PaymentResultVo statusResult = paymentService.queryPaymentStatus(orderNo);
        System.out.println("最终支付状态: " + statusResult.getStatus());

        System.out.println("\n=== 支付失败流程测试完成 ===");
    }
}