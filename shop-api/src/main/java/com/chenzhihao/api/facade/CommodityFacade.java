package com.chenzhihao.api.facade;

import com.chenzhihao.api.dto.CommodityDTO;
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


}
