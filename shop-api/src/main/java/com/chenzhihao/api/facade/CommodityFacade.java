package com.chenzhihao.api.facade;

import com.chenzhihao.api.dto.CommodityDTO;

import java.util.List;
import java.util.Set;

public interface CommodityFacade {

    List<CommodityDTO> queryCommodityByIds(Set<Long> commodityIds);

}
