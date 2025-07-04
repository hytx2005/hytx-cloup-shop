package com.chenzhihao.api.facade;

import com.chenzhihao.api.dto.CommodityDTO;
import com.chenzhihao.api.dto.OrderCreDto;
import com.chenzhihao.api.vo.CommodityPayVo;

import java.util.List;
import java.util.Set;

public interface CommodityFacade {

    /**
     * 根据商品id集合返回商品集合信息
     * @param commodityIds 商品id集合
     * @return {@link List }<{@link CommodityDTO }>
     */
    List<CommodityDTO> queryCommodityByIds(Set<Long> commodityIds);


    /**
     * 根据商品id进行商品数量扣减，返回商品信息
     * @param orderDto 商品id和购买的数量 的集合
     * @return {@link List }<{@link CommodityPayVo }>
     */
    List<CommodityPayVo> getCommodityById(OrderCreDto orderDto);
}
