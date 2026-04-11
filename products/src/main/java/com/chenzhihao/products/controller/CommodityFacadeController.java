package com.chenzhihao.products.controller;

import cn.hutool.core.bean.BeanUtil;
import com.chenzhihao.api.dto.CommodityDTO;
import com.chenzhihao.products.service.ICommodityService;
import com.chenzhihao.shopcommon.util.UserContext;
import com.chenzhihao.shopcommon.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation**;

import java.util.List;
import java.util.Set;

/**
 * Commodity Facade REST API (replaces Dubbo CommodityFacadeImp)
 * @author ASUS
 */
@RestController
@RequestMapping("/api/internal/commodities")
public class CommodityFacadeController {

    @Autowired
    private ICommodityService commodityService;

    /**
     * 根据商品id集合返回商品集合信息 (Dubbo queryCommodityByIds equivalent)
     * @param commodityIds 商品id集合
     * @return Result<List<CommodityDTO>>
     */
    @PostMapping("/query-by-ids")
    public Result<List<CommodityDTO>> queryCommodityByIds(@RequestBody CommodityIdsRequest request) {
        // 测试filter是否生效 (equivalent to Dubbo version)
        Long userId = UserContext.getUserId();
        System.out.println("在queryCommodityByIds方法中userId:" + userId);

        List<CommodityDTO> result = BeanUtil.copyToList(
            commodityService.listByIds(request.getCommodityIds()),
            CommodityDTO.class
        );
        return Result.success(result);
    }

    /**
     * 释放商品库存
     * @param request ReleaseStockRequest containing commodity ID and quantity
     * @return Result<Boolean>
     */
    @PostMapping("/release-stock")
    public Result<Boolean> releaseStock(@RequestBody ReleaseStockRequest request) {
        // Implementation for releasing stock
        // This would typically update the commodity inventory
        System.out.println("Releasing stock for commodity: " + request.getCommodityId() +
                          ", quantity: " + request.getQuantity());
        return Result.success(true);
    }

    public static class CommodityIdsRequest {
        private Set<Long> commodityIds;

        public Set<Long> getCommodityIds() {
            return commodityIds;
        }

        public void setCommodityIds(Set<Long> commodityIds) {
            this.commodityIds = commodityIds;
        }
    }

    public static class ReleaseStockRequest {
        private Long commodityId;
        private Integer quantity;

        public Long getCommodityId() {
            return commodityId;
        }

        public void setCommodityId(Long commodityId) {
            this.commodityId = commodityId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}