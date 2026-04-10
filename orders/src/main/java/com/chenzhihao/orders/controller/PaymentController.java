package com.chenzhihao.orders.controller;

import com.chenzhihao.orders.domain.dto.PaymentCallbackDto;
import com.chenzhihao.orders.domain.dto.PaymentRequestDto;
import com.chenzhihao.orders.domain.vo.PaymentResultVo;
import com.chenzhihao.orders.service.IPaymentService;
import com.chenzhihao.shopcommon.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 支付控制器
 *
 * @author Claude
 */
@RestController
@RequestMapping("/orders/payment")
public class PaymentController {

    @Autowired
    private IPaymentService paymentService;

    /**
     * 创建支付请求
     *
     * @param paymentRequestDto 支付请求信息
     * @return 支付结果
     */
    @PostMapping("/create")
    public Result<PaymentResultVo> createPayment(@RequestBody PaymentRequestDto paymentRequestDto) {
        PaymentResultVo result = paymentService.createPayment(paymentRequestDto);
        return Result.success(result);
    }

    /**
     * 查询支付状态
     *
     * @param orderNo 订单号
     * @return 支付结果
     */
    @GetMapping("/status/{orderNo}")
    public Result<PaymentResultVo> queryPaymentStatus(@PathVariable String orderNo) {
        PaymentResultVo result = paymentService.queryPaymentStatus(orderNo);
        return Result.success(result);
    }

    /**
     * 支付回调接口（用于第三方支付平台回调）
     *
     * @param callbackDto 支付回调信息
     * @return 处理结果
     */
    @PostMapping("/callback")
    public Result<String> paymentCallback(@RequestBody PaymentCallbackDto callbackDto) {
        boolean success = paymentService.handlePaymentCallback(callbackDto);
        if (success) {
            return Result.success("回调处理成功");
        } else {
            return Result.error("回调处理失败");
        }
    }

    /**
     * 模拟支付接口（用于本地测试）
     *
     * @param orderNo 订单号
     * @return 支付结果
     */
    @GetMapping("/simulate/{orderNo}")
    public Result<String> simulatePayment(@PathVariable String orderNo) {
        // 创建模拟支付回调
        PaymentCallbackDto callbackDto = new PaymentCallbackDto();
        callbackDto.setOrderNo(orderNo);
        callbackDto.setPayStatus("SUCCESS");
        callbackDto.setPayTime(System.currentTimeMillis());
        callbackDto.setTradeNo("SIM" + System.currentTimeMillis());

        // 计算订单总金额
        // 这里应该从数据库查询订单金额，简化处理
        callbackDto.setAmount(java.math.BigDecimal.valueOf(100.00));

        // 生成签名
        String sign = Integer.toHexString(
                (orderNo + callbackDto.getAmount() + callbackDto.getPayStatus() + "payment_secret_key_2024").hashCode()
        );
        callbackDto.setSign(sign);

        boolean success = paymentService.handlePaymentCallback(callbackDto);
        if (success) {
            return Result.success("模拟支付成功");
        } else {
            return Result.error("模拟支付失败");
        }
    }

    /**
     * 支付页面（模拟支付界面）
     *
     * @param orderNo 订单号
     * @return 支付页面HTML
     */
    @GetMapping("/page")
    @ResponseBody
    public String paymentPage(
            @RequestParam String orderNo,
            @RequestParam java.math.BigDecimal amount,
            @RequestParam String method) {

        return "<!DOCTYPE html>" +
                "<html><head><title>支付页面</title></head>" +
                "<body style='text-align:center; margin-top:100px;'>" +
                "<h1>订单支付</h1>" +
                "<p>订单号: " + orderNo + "</p>" +
                "<p>支付金额: ¥" + amount + "</p>" +
                "<p>支付方式: " + method + "</p>" +
                "<button onclick='pay()' style='padding:10px 20px; font-size:16px;'>确认支付</button>" +
                "<script>" +
                "function pay() {" +
                "  fetch('/orders/payment/simulate/" + orderNo + "', { method: 'GET' })" +
                "    .then(response => response.json())" +
                "    .then(data => {" +
                "      if(data.code === 1) {" +
                "        alert('支付成功！');" +
                "        window.location.href = '/orders/payment/status/" + orderNo + "';" +
                "      } else {" +
                "        alert('支付失败：' + data.msg);" +
                "      }" +
                "    });" +
                "}" +
                "</script>" +
                "</body></html>";
    }
}