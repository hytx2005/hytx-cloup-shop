package com.chenzhihao.carts.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chenzhihao.carts.domain.dto.CartDTO;
import com.chenzhihao.carts.domain.po.Cart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chenzhihao.carts.domain.vo.CartVO;
import com.chenzhihao.carts.mapper.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 购物车模块Service接口
 * 提供购物车相关的业务逻辑处理，继承MyIService以获得基础CRUD功能
 *
 * @author hqh
 * @since 2025-07-03
 */
public interface ICartService extends IService<Cart> {
    /**
     * 添加商品到购物车
     * 如果商品已存在，则增加数量；否则新增购物车记录
     *
     * @param cartDTO 购物车DTO，包含商品ID和数量
     */
    void addCart(CartDTO cartDTO);

    /**
     * 查询当前用户的购物车
     * 返回用户购物车中所有商品的详细信息
     *
     * @return 购物车商品列表
     */
    List<CartVO> queryMyCarts();

    /**
     * 同步购物车中的商品信息
     * 将商品服务的最新商品信息同步到购物车，保持数据一致性
     */
    void syncCartCommodities();

    /**
     * 从购物车移除商品
     * 根据商品ID列表批量移除购物车中的商品
     *
     * @param itemIds 要移除的商品ID列表
     */
    void removeByCommodityIds(Collection<Long> itemIds);
}
