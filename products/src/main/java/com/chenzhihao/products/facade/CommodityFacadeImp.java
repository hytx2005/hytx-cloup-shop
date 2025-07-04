package com.chenzhihao.products.facade;

import cn.hutool.core.bean.BeanUtil;
import com.chenzhihao.api.dto.CommodityDTO;
import com.chenzhihao.api.facade.CommodityFacade;
import com.chenzhihao.products.service.ICommodityService;
import com.chenzhihao.shopcommon.util.UserContext;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
@Component
@DubboService
public class CommodityFacadeImp implements CommodityFacade {

    @Autowired
    private ICommodityService commodityService;

    @Override
    public List<CommodityDTO> queryCommodityByIds(Set<Long> commodityIds) {
        //测试filter是否生效
        Long userId = UserContext.getUserId();
        System.out.println("在queryCommodityByIds方法中userId:" + userId);
        return BeanUtil.copyToList(commodityService.listByIds(commodityIds), CommodityDTO.class);
    }
}
