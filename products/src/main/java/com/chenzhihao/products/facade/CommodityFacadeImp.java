package com.chenzhihao.products.facade;

import cn.hutool.core.bean.BeanUtil;
import com.chenzhihao.api.dto.CommodityDTO;
import com.chenzhihao.api.dto.OrderCreDto;
import com.chenzhihao.api.dto.OrderDetailDto;
import com.chenzhihao.api.facade.CommodityFacade;
import com.chenzhihao.api.vo.CommodityPayVo;
import com.chenzhihao.products.domain.po.Commodity;
import com.chenzhihao.products.mapper.mp.CommodityMapper;
import com.chenzhihao.products.service.ICommodityService;
import com.chenzhihao.shopcommon.exception.BaseException;
import com.chenzhihao.shopcommon.result.Result;
import com.chenzhihao.shopcommon.util.UserContext;
import org.apache.dubbo.config.annotation.DubboService;
import org.redisson.api.RList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

/**
 * 商品模块的dubbo服务
 * @author ASUS
 */
@Component
@DubboService
public class CommodityFacadeImp implements CommodityFacade {

    @Autowired
    private ICommodityService commodityService;
    @Autowired
    private CommodityMapper commodityMapper;

    @Override
    public List<CommodityDTO> queryCommodityByIds(Set<Long> commodityIds) {
        //测试filter是否生效
        Long userId = UserContext.getUserId();
        System.out.println("在queryCommodityByIds方法中userId:" + userId);
        return BeanUtil.copyToList(commodityService.listByIds(commodityIds), CommodityDTO.class);
    }


    /**
     * 根据商品id进行商品数量扣减，返回商品信息
     * @param orderDto 商品id和购买的数量 的集合
     * @return {@link List }<{@link CommodityPayVo }>
     */
    @Override
    @Transactional(rollbackFor = BaseException.class)
    public List<CommodityPayVo> getCommodityById(OrderCreDto orderDto) {

        // 1.获取到商品id集合并排序
        List<OrderDetailDto> details = orderDto.getDetails();
        List<Long> commodityIds  = new ArrayList<>();
        for (OrderDetailDto dto : details) {
            commodityIds.add(dto.getCommodityId());
        }
            // 商品按id排序
        Collections.sort(commodityIds);


        // 2.使用悲观锁查询商品信息
        List<Commodity> commodities = commodityMapper.selectCommoditiesForUpdate(commodityIds);

        // 结果
        List<CommodityPayVo> result = new ArrayList<>();

        // 3.将商品信息存入map集合
        Map<Long, Commodity> commodityMap = new HashMap<>();
        for (Commodity commodity : commodities) {
            commodityMap.put(commodity.getId(), commodity);
        }
        for (OrderDetailDto detail : details) {
            Long commodityId = detail.getCommodityId();
            Integer num = detail.getNum();
            Commodity commodity = commodityMap.get(commodityId);

            if (commodity == null || commodity.getStock() - commodity.getSold() < num){
                throw new RuntimeException("库存不足");
            }
            commodity.setSold(commodity.getSold() + num);
            commodityMap.put(commodityId, commodity);

            CommodityPayVo commodityPayVo = CommodityPayVo.builder()
                    .id(commodityId)
                    .price(commodity.getPrice())
                    .name(commodity.getName())
                    .imageUrl(commodity.getImageUrl())
                    .build();

            result.add(commodityPayVo);
            commodityMapper.batchUpdateSold(commodity);
        }
        return result;
    }

    /**
     * 释放商品库存（用于订单取消或支付失败时）
     * @param commodityId 商品id
     * @param quantity 释放数量
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean releaseStock(Long commodityId, Integer quantity) {
        if (commodityId == null || quantity == null || quantity <= 0) {
            return false;
        }

        // 使用悲观锁查询商品
        Commodity commodity = commodityMapper.selectCommodityForUpdate(commodityId);
        if (commodity == null) {
            return false;
        }

        // 释放库存：已售数量减少
        if (commodity.getSold() >= quantity) {
            commodity.setSold(commodity.getSold() - quantity);
            commodityMapper.batchUpdateSold(commodity);
            return true;
        }

        return false;
    }




}
