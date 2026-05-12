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
 * 购物车服务REST API接口
 * 替代原有的Dubbo CartFacadeImpl实现
 * @author dhx
 */
@RestController
@RequestMapping("/api/internal/carts")
public class CartFacadeController {

    @Autowired
    private CartMapper cartMapper;

    /**
     * 从购物车中删除用户的对应商品
     * 替代原有的Dubbo deleteCart方法
     * @param request 删除购物车商品的请求对象
     * @return 删除操作结果
     */
    @DeleteMapping("/delete")
    public Result<Boolean> deleteCart(@RequestBody com.chenzhihao.api.client.CartClient.DeleteCartRequest request) {
        Long user = UserContext.getUserId();
        // 构建删除条件
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", user);
        wrapper.in("commodity_id", request.getCommodityIds());
        int deleted = cartMapper.delete(wrapper);
        return Result.success(deleted > 0);
    }

}