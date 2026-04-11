package com.chenzhihao.orders.facade;

import com.chenzhihao.api.dto.OrderForPay;
import com.chenzhihao.api.facade.OrderFacade;
import com.chenzhihao.orders.domain.po.Orders;
import com.chenzhihao.orders.mapper.OrdersMapper;
import com.chenzhihao.shopcommon.exception.BaseException;
import com.chenzhihao.shopcommon.util.UserContext;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单模块提供的dubbo服务
 * @author dhx
 */
@DubboService
@Component
public class OrderFacadeImp implements OrderFacade {

    private OrdersMapper ordersMapper;
    @Autowired
    public void setOrdersMapper(OrdersMapper ordersMapper) {
        this.ordersMapper = ordersMapper;
    }

    /**
     * 根据商品信息和订单号生成预支付订单
     * @param pays    商品信息集合
     * @param orderNo 订单号
     */
    @Override
    public void createOrder(List<OrderForPay> pays,String orderNo) {
        Long userId = UserContext.getUserId();
        if (userId == null){
            throw new BaseException("获取用户数据失败");
        }
        // 2.生成订单表数据
        for (OrderForPay vo : pays) {
            BigDecimal money = vo.getPrice().multiply(new BigDecimal(vo.getNum()));
            Orders orders = Orders.builder()
                    .userId(userId)
                    .orderNo(orderNo)
                    .commodityId(vo.getId())
                    .commodityName(vo.getName())
                    .commodityNum(vo.getNum())
                    .commodityUrl(vo.getImageUrl())
                    .money(money)
                    .payStatus("未支付")
                    .build();
            ordersMapper.insert(orders);
        }
    }
}
