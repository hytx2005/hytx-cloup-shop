package com.chenzhihao.carts.service.Imp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chenzhihao.api.dto.CommodityDTO;
import com.chenzhihao.api.client.CommodityClient;
import com.chenzhihao.carts.domain.dto.CartDTO;
import com.chenzhihao.carts.domain.po.Cart;
import com.chenzhihao.carts.domain.vo.CartVO;
import com.chenzhihao.carts.mapper.CartMapper;
import com.chenzhihao.carts.service.ICartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenzhihao.shopcommon.util.UserContext;
import com.chenzhihao.shopcommon.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 购物车服务实现类
 *
 * @author hqh
 * @since 2025-07-03
 */
@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {

    private final CartMapper cartMapper;
    @Autowired
    private CommodityClient commodityClient;

    @Autowired
    public CartServiceImpl(CartMapper cartMapper) {
        this.cartMapper = cartMapper;
    }

    @Override
    public void addCart(CartDTO cartDTO) {
        // 1.获取登录用户
        Long userId = UserContext.getUserId();
        // 2. 尝试直接更新数量
        boolean success = cartMapper.updateNum(cartDTO.getCommodityId(), userId);
        // 3. 如果更新失败（影响行数为0），说明购物车中不存在该商品，则执行新增
        if (!success) {
            // 3.1.转换PO
            Cart cart = BeanUtil.copyProperties(cartDTO, Cart.class);
            // 3.2.保存当前用户
            cart.setUserId(userId);
            // 3.3.保存到数据库
            save(cart);
        }
    }

    @Override
    public List<CartVO> queryMyCarts() {
        // 1.查询我的购物车列表
        Long userId = UserContext.getUserId();
        List<Cart> carts = cartMapper.selectList(new QueryWrapper<Cart>()
                .eq("user_id", userId));
        if (CollUtil.isEmpty(carts)) {
            return Collections.emptyList();
        }
        // 2.转换VO
        List<CartVO> vos = BeanUtil.copyToList(carts, CartVO.class);

        // 3.处理VO中的商品信息
        handleCartCommodities(vos);
        for (CartVO vo : vos) {
            cartMapper.updateCartByCommodity(vo);
        }

        // 4.返回
        return vos;
    }

    private void handleCartCommodities(List<CartVO> vos) {
        // 1.获取商品id
        Set<Long> commodityIds = vos.stream().map(CartVO::getCommodityId).collect(Collectors.toSet());
        // 2.查询商品
        CommodityClient.CommodityIdsRequest request = new CommodityClient.CommodityIdsRequest();
        request.setCommodityIds(commodityIds);
        Result<List<CommodityDTO>> result = commodityClient.queryCommodityByIds(request);
        List<CommodityDTO> commodities = result.getData();
        if (CollUtil.isEmpty(commodities)) {
            return;
        }
        // 3.转为 id 到 commodity 的map
        Map<Long, CommodityDTO> commodityMap = commodities.stream().collect(Collectors.toMap(CommodityDTO::getId, Function.identity()));
        // 4.写入vo
        for (CartVO v : vos) {
            CommodityDTO commodity = commodityMap.get(v.getCommodityId());
            if (commodity == null) {
                continue;
            }
            v.setCommodityPrice(commodity.getPrice());
            v.setCommodityUrl(commodity.getImageUrl());
        }
    }

    @Override
    public void removeByCommodityIds(Collection<Long> commodityIds) {
        // 1.构建删除条件
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("user_id", UserContext.getUserId())
                .in("commodity_id", commodityIds);
        // 2.删除
        remove(queryWrapper);
    }


}
