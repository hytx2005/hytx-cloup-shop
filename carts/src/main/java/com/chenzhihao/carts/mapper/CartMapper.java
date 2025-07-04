package com.chenzhihao.carts.mapper;

import com.chenzhihao.carts.domain.po.Cart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenzhihao.carts.domain.vo.CartVO;
import org.apache.ibatis.annotations.Param;
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


    /**
     * 更新购物车表的内容
     * @param vo
     */
    @Update("update cart set commodity_name = #{vo.commodityName}, commodity_price = #{vo.commodityPrice}, " +
            "spec = #{vo.spec}, commodity_url = #{vo.commodityUrl} " +
            "where commodity_id = #{vo.commodityId} and user_id = #{vo.userId} and id = #{vo.id}")
    void updateCartByCommodity(@Param("vo") CartVO vo);
}
