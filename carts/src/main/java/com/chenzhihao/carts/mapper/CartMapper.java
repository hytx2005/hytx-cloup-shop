package com.chenzhihao.carts.mapper;

import com.chenzhihao.carts.domain.po.Cart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 购物车 Mapper 接口
 * </p>
 *
 * @author hqh
 * @since 2025-07-03
 */
public interface CartMapper extends BaseMapper<Cart> {

    @Update("UPDATE cart SET commodity_num = commodity_num + 1 WHERE commodity_id = #{commodityId} AND user_id = #{userId}")
    boolean updateNum(Long commodityId, Long userId);
}
