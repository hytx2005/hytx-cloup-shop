package com.chenzhihao.carts.controller;


import com.chenzhihao.carts.domain.dto.CartDTO;
import com.chenzhihao.carts.domain.vo.CartVO;
import com.chenzhihao.carts.service.ICartService;
import com.chenzhihao.shopcommon.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车模块Controller
 * 提供购物车相关的REST API接口，包括添加、查询和删除功能
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
     * 添加商品到购物车
     * 如果商品已存在，则增加数量；否则新增购物车记录
     *
     * @param cartDTO 购物车DTO，包含商品ID和数量
     * @return 添加操作结果
     */
    @PostMapping
    public Result<?> addCart(@RequestBody CartDTO cartDTO) {
        cartService.addCart(cartDTO);
        return Result.success();
    }

    /**
     * 查询当前用户的购物车
     * 返回用户购物车中所有商品的详细信息
     *
     * @return 购物车商品列表
     */
    @GetMapping
    public Result<List<CartVO>> queryMyCarts() {
        return Result.success(cartService.queryMyCarts());
    }

    /**
     * 从购物车移除商品
     * 根据商品ID列表批量移除购物车中的商品
     *
     * @param commodityIds 要移除的商品ID列表
     * @return 移除操作结果
     */
    @DeleteMapping
    public Result<?> removeByCommodityIds(@RequestBody List<Long> commodityIds) {
        cartService.removeByCommodityIds(commodityIds);
        return Result.success();
    }

}
