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

    @PostMapping
    public Result<?> addCart(@RequestBody CartDTO cartDTO) {
        cartService.addCart(cartDTO);
        return Result.success();
    }

    @GetMapping
    public Result<List<CartVO>> queryMyCarts() {
        return Result.success(cartService.queryMyCarts());
    }

    @DeleteMapping
    public Result<?> removeByCommodityIds(@RequestBody List<Long> commodityIds) {
        cartService.removeByCommodityIds(commodityIds);
        return Result.success();
    }

}
