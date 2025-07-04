package com.chenzhihao.carts.controller;


import com.chenzhihao.carts.domain.dto.CartDTO;
import com.chenzhihao.carts.domain.vo.CartVO;
import com.chenzhihao.carts.service.ICartService;
import com.chenzhihao.shopcommon.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 购物车 前端控制器
 * </p>
 *
 * @author hqh
 * @since 2025-07-03
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private ICartService cartService;

    /**
     * 添加到购物车
     * @param cartDTO 购物车实体类
     * @return {@link Result }<{@link ? }>
     */
    @PostMapping
    public Result<?> addCart(@RequestBody CartDTO cartDTO) {
        cartService.addCart(cartDTO);
        return Result.success();
    }

    /**
     * 查询我的购物车
     * @return {@link Result }<{@link List }<{@link CartVO }>>
     */
    @GetMapping
    public Result<List<CartVO>> queryMyCarts() {
        return Result.success(cartService.queryMyCarts());
    }

    /**
     * 移除购物车
     * @param commodityIds
     * @return {@link Result }<{@link ? }>
     */
    @DeleteMapping
    public Result<?> removeByCommodityIds(@RequestBody List<Long> commodityIds) {
        cartService.removeByCommodityIds(commodityIds);
        return Result.success();
    }

}
