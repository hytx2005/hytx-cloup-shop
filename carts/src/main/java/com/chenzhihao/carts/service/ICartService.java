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
 * <p>
 * 购物车 服务类
 * </p>
 *
 * @author hqh
 * @since 2025-07-03
 */
public interface ICartService extends IService<Cart> {
    /**
     * 添加购物车
     * @param cartDTO 购物车实体类
     */
    void addCart(CartDTO cartDTO);

    /**
     * 查询购物车
     * @return {@link List }<{@link CartVO }>
     */
    List<CartVO> queryMyCarts();

    /**
     * 同步购物车中的商品信息
     * 将商品服务的最新商品信息同步到购物车
     */
    void syncCartCommodities();

    /**
     * 移除购物车
     * @param itemIds 商品id集合
     */
    void removeByCommodityIds(Collection<Long> itemIds);
}
