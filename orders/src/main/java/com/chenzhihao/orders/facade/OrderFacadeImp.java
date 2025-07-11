package com.chenzhihao.orders.facade;

import com.chenzhihao.api.dto.OrderForPay;
import com.chenzhihao.api.facade.OrderFacade;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * 订单模块提供的dubbo服务
 * @author dhx
 */
@DubboService
public class OrderFacadeImp implements OrderFacade {
    @Override
    public int createOrder(List<OrderForPay> pays) {
        return 0;
    }
}
