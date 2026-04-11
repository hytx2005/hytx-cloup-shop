package com.chenzhihao.orders.test;

// import com.chenzhihao.api.dto.OrderCreDto;
// import com.chenzhihao.api.dto.OrderDetailDto;
import com.chenzhihao.orders.controller.OrdersController;
import com.chenzhihao.orders.domain.dto.PaymentRequestDto;
import com.chenzhihao.orders.domain.vo.PaymentResultVo;
import com.chenzhihao.orders.service.IPaymentService;
import com.chenzhihao.orders.util.PaymentTestUtil;
import com.chenzhihao.shopcommon.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * 支付流程测试类
 * 用于演示和测试完整的支付流程
 *
 * @author Claude
 */
@Component
public class PaymentFlowTest implements CommandLineRunner {

    @Autowired
    private OrdersController ordersController;

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private PaymentTestUtil paymentTestUtil;

    @Override
    public void run(String... args) throws Exception {
        // 此方法会在Spring Boot应用启动后执行
        // 可以用于演示支付流程
        System.out.println("=== 支付系统演示 ===");

        // 注意：以下代码仅为演示，实际使用时需要真实的用户登录状态和商品数据
        // demo();
    }

    /**
     * 演示完整的支付流程
     */
    public void demo() {
        try {
            System.out.println("开始演示支付流程...");

            // 1. 创建订单（需要真实的用户和商品数据）
            // OrderCreDto orderDto = new OrderCreDto();
            // OrderDetailDto detail = new OrderDetailDto();
            // detail.setCommodityId(1L);
            // detail.setNum(2);
            // orderDto.setDetails(Arrays.asList(detail));

            // Result<String> createResult = ordersController.createOrder(orderDto);
            // if (createResult.getCode() == 1) {
            //     String orderNo = createResult.getData();
                // System.out.println("订单创建成功: " + orderNo);

                // 2. 创建支付请求
                PaymentRequestDto paymentRequest = new PaymentRequestDto();
                // paymentRequest.setOrderNo(orderNo);
                paymentRequest.setAmount(new BigDecimal("199.98"));
                paymentRequest.setPayMethod("SIMULATE");
                paymentRequest.setDescription("购买商品测试");

                PaymentResultVo paymentResult = paymentService.createPayment(paymentRequest);
                System.out.println("支付请求创建成功: " + paymentResult.getStatus());

                // 3. 执行模拟支付
            // if ("SUCCESS".equals(paymentResult.getStatus())) {
            //     System.out.println("模拟支付成功！");
            // }

            // 4. 查询支付状态
            // PaymentResultVo statusResult = paymentService.queryPaymentStatus(orderNo);
            // System.out.println("最终支付状态: " + statusResult.getStatus());
            // }

        } catch (Exception e) {
            System.out.println("演示过程中出现异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试支付超时流程
     */
    public void testTimeoutFlow() {
        try {
            System.out.println("\n=== 测试支付超时流程 ===");

            // 创建订单后不进行支付，等待15分钟后查看是否自动取消
            // OrderCreDto orderDto = new OrderCreDto();
            // OrderDetailDto detail = new OrderDetailDto();
            // detail.setCommodityId(1L);
            // detail.setNum(1);
            // orderDto.setDetails(Arrays.asList(detail));

            // Result<String> createResult = ordersController.createOrder(orderDto);
            // if (createResult.getCode() == 1) {
            //     String orderNo = createResult.getData();
                // System.out.println("订单创建成功: " + orderNo);
                System.out.println("订单已添加到延迟队列，15分钟后将自动取消");
            // System.out.println("可以使用以下命令手动测试支付状态:");
            // System.out.println("GET /orders/payment/status/" + orderNo);
            // }

        } catch (Exception e) {
            System.out.println("测试超时流程时出现异常: " + e.getMessage());
        }
    }
}