package com.chenzhihao.api.client;

import com.chenzhihao.shopcommon.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 订单服务OpenFeign客户端接口
 * 用于其他服务通过OpenFeign调用订单服务的功能
 *
 * @author hqh
 */
@FeignClient(name = "orders-service")
public interface OrderClient {

    /**
     * 创建订单
     * 根据订单详情创建订单记录，用于支付流程中的订单创建
     *
     * @param request 包含订单号和商品项列表的请求对象
     * @return 创建订单操作结果
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/internal/orders/create")
    Result<Boolean> createOrder(@RequestBody CreateOrderRequest request);

    /**
     * 创建订单的请求参数封装类
     */
    class CreateOrderRequest {
        /**
         * 订单商品项列表
         * 订单中包含的所有商品项
         */
        private List<OrderItem> orderItems;

        /**
         * 订单号
         * 订单的唯一标识
         */
        private String orderNo;

        public List<OrderItem> getOrderItems() {
            return orderItems;
        }

        public void setOrderItems(List<OrderItem> orderItems) {
            this.orderItems = orderItems;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }
    }

    /**
     * 订单商品项结构
     * 描述订单中单个商品的详细信息
     */
    class OrderItem {
        /**
         * 商品ID
         * 商品的唯一标识
         */
        private Long id;

        /**
         * 商品名称
         */
        private String name;

        /**
         * 商品图片URL
         */
        private String imageUrl;

        /**
         * 商品数量
         * 购买的商品数量
         */
        private Integer num;

        /**
         * 商品价格
         * 单位为元
         */
        private java.math.BigDecimal price;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public Integer getNum() {
            return num;
        }

        public void setNum(Integer num) {
            this.num = num;
        }

        public java.math.BigDecimal getPrice() {
            return price;
        }

        public void setPrice(java.math.BigDecimal price) {
            this.price = price;
        }
    }
}