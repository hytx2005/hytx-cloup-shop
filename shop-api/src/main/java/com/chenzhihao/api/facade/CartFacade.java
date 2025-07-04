package com.chenzhihao.api.facade;

import java.util.List;

/**
 * 商品模块提供的dubbo服务
 * @author dhx
 */
public interface CartFacade {

    /**
     * 从购物车中删除用户的对应商品
     * @param commodityIds 商品id集合
     * @return boolean
     */
    boolean deleteCart(List<Long> commodityIds);
}
