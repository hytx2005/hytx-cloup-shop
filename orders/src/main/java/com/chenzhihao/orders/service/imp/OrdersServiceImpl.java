package com.chenzhihao.orders.service.imp;

import com.chenzhihao.api.dto.OrderCreDto;
import com.chenzhihao.api.dto.OrderDetailDto;
import com.chenzhihao.api.facade.CartFacade;
import com.chenzhihao.api.facade.CommodityFacade;
import com.chenzhihao.api.vo.CommodityPayVo;
import com.chenzhihao.orders.domain.po.Orders;
import com.chenzhihao.orders.mapper.OrdersMapper;
import com.chenzhihao.orders.service.IOrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenzhihao.shopcommon.annotation.DubboServiceAop;
import com.chenzhihao.shopcommon.exception.BaseException;
import com.chenzhihao.shopcommon.util.OrderNoUtil;
import com.chenzhihao.shopcommon.util.UserContext;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单模块 服务实现类
 * </p>
 *
 * @author hqh
 * @since 2025-06-27
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

    @DubboReference
    private CommodityFacade commodityFacade;
    @DubboReference
    private CartFacade cartFacade;

    @Autowired
    private OrdersMapper ordersMapper;
    /**
     * 生成订单号
     *  1.扣减商品数量
     *  2.生成订单表数据
     *  3.去购物车中删除对应数据
     * @param orderCreDto 订单信息
     * @return {@link String }
     */
    @Override
    @DubboServiceAop(message = "商品库存不足")
    public String createOrder(OrderCreDto orderCreDto) {
        Long userId = UserContext.getUserId();
        if (userId == null){
            throw new BaseException("用户未登录");
        }
        // 1.扣减商品数量，返回商品数据
        List<CommodityPayVo> commodityById = commodityFacade.getCommodityById(orderCreDto);

        Map<Long, Integer> map = new HashMap<>();
        List<Long> ids = new ArrayList<>();
        for (OrderDetailDto detail : orderCreDto.getDetails()) {
            map.put(detail.getCommodityId(), detail.getNum());
            ids.add(detail.getCommodityId());
        }

        String orderNo = OrderNoUtil.generateOrderNo();
        // 2.生成订单表数据
        for (CommodityPayVo vo : commodityById) {
            Integer num = map.get(vo.getId());
            BigDecimal money = vo.getPrice().multiply(new BigDecimal(num));
            Orders orders = Orders.builder()
                    .userId(userId)
                    .orderNo(orderNo)
                    .commodityId(vo.getId())
                    .commodityName(vo.getName())
                    .commodityNum(num)
                    .commodityUrl(vo.getImageUrl())
                    .money(money)
                    .payStatus("未支付")
                    .build();
            ordersMapper.insert(orders);
        }

        // 3.去购物车中删除对应数据
        cartFacade.deleteCart(ids);
        return orderNo;
    }
}
