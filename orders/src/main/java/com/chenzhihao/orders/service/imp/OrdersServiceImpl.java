package com.chenzhihao.orders.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
    public void setOrdersMapper(OrdersMapper ordersMapper) {
        this.ordersMapper = ordersMapper;
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
