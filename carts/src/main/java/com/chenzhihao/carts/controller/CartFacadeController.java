package com.chenzhihao.carts.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chenzhihao.carts.domain.po.Cart;
import com.chenzhihao.carts.mapper.CartMapper;
import com.chenzhihao.shopcommon.util.UserContext;
import com.chenzhihao.shopcommon.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Cart Facade REST API (replaces Dubbo CartFacadeImpl)
 * @author dhx
 */
@RestController
@RequestMapping("/api/internal/carts")
public class CartFacadeController {

    @Autowired
    private CartMapper cartMapper;

    /**
     * 从购物车中删除用户的对应商品 (Dubbo deleteCart equivalent)
     * @param commodityIds 商品id集合
     * @return Result<Boolean>
     */
    @DeleteMapping("/delete")
    public Result<Boolean> deleteCart(@RequestBody DeleteCartRequest request) {
        Long user = UserContext.getUserId();
        // 构建删除条件
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", user);
        wrapper.in("commodity_id", request.getCommodityIds());
        int deleted = cartMapper.delete(wrapper);
        return Result.success(deleted > 0);
    }

    public static class DeleteCartRequest {
        private List<Long> commodityIds;

        public List<Long> getCommodityIds() {
            return commodityIds;
        }

        public void setCommodityIds(List<Long> commodityIds) {
            this.commodityIds = commodityIds;
        }
    }
}