package com.chenzhihao.orders.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chenzhihao.api.dto.OrderCreDto;
import com.chenzhihao.api.dto.OrderDetailDto;
import com.chenzhihao.api.facade.CartFacade;
import com.chenzhihao.api.facade.CommodityFacade;
import com.chenzhihao.api.vo.CommodityPayVo;
import com.chenzhihao.orders.domain.dto.OrderDelDto;
import com.chenzhihao.orders.domain.po.Orders;
import com.chenzhihao.orders.mapper.OrdersMapper;
import com.chenzhihao.orders.service.IOrdersService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenzhihao.shopcommon.exception.BaseException;
import com.chenzhihao.shopcommon.util.OrderNoUtil;
import com.chenzhihao.shopcommon.util.UserContext;
import com.chenzhihao.orders.util.DelayedQueueUtil;
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

    private OrdersMapper ordersMapper;

    @Autowired
    private DelayedQueueUtil delayedQueueUtil;

    @Autowired
    public void setOrdersMapper(OrdersMapper ordersMapper) {
        this.ordersMapper = ordersMapper;
    }
    /**
     * 生成订单号
     *  1.扣减商品数量
     *  2.生成订单表数据
     *  3.去购物车中删除对应数据
     * @param orderCreDto 订单信息
     * @return {@link String }
     */
    @Override

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
                    .payStatus("PENDING")
                    .createTime(new java.util.Date())
                    .updateTime(new java.util.Date())
                    .build();
            ordersMapper.insert(orders);
        }

        // 3.去购物车中删除对应数据
        cartFacade.deleteCart(ids);

        // 4.将订单添加到延迟队列（15分钟后超时取消）
        delayedQueueUtil.addOrderToTimeoutQueue(orderNo);

        return orderNo;
    }

    /**
     * 根据订单id集合删除id
     * @param dto 订单信息集合
     * @return boolean
     */
    @Override
    public boolean deleteOrders(OrderDelDto dto) {
        // 构造删除条件  id in IDs 并且 userId = 当前登录用户id
        QueryWrapper<Orders> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", dto.getIds());
        queryWrapper.eq("user_id", UserContext.getUserId());
        int delete = ordersMapper.delete(queryWrapper);
        return delete > 0;
    }
}
