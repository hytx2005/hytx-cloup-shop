package com.chenzhihao.carts.facade;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chenzhihao.api.facade.CartFacade;
import com.chenzhihao.carts.domain.po.Cart;
import com.chenzhihao.carts.mapper.CartMapper;
import com.chenzhihao.shopcommon.util.UserContext;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
@DubboService
public class CartFacadeImpl implements CartFacade {

    @Autowired
    private CartMapper cartMapper;
    /**
     * 从购物车中删除用户的对应商品
     * @param commodityIds 商品id集合
     * @return boolean
     */
    @Override
    public boolean deleteCart(List<Long> commodityIds) {
        Long user = UserContext.getUserId();
        // 构建删除条件
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", user);
        wrapper.in("commodity_id", commodityIds);
        cartMapper.delete(wrapper);
        return false;
    }
}
