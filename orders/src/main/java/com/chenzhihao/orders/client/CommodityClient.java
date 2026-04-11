package com.chenzhihao.orders.client;

import com.chenzhihao.api.dto.CommodityDTO;
import com.chenzhihao.shopcommon.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Set;

/**
 * OpenFeign client for Commodity Service (replaces @DubboReference CommodityFacade)
 * @author dhx
 */
@FeignClient(name = "products-service")
public interface CommodityClient {

    /**
     * Query commodities by IDs (equivalent to CommodityFacade.queryCommodityByIds)
     * @param request CommodityIdsRequest containing commodity IDs
     * @return Result<List<CommodityDTO>>
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/internal/commodities/query-by-ids")
    Result<List<CommodityDTO>> queryCommodityByIds(@RequestBody CommodityIdsRequest request);

    /**
     * Release stock for a commodity
     * @param request ReleaseStockRequest containing commodity ID and quantity
     * @return Result<Boolean>
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/internal/commodities/release-stock")
    Result<Boolean> releaseStock(@RequestBody ReleaseStockRequest request);

    /**
     * Inner class to match the request structure
     */
    class CommodityIdsRequest {
        private Set<Long> commodityIds;

        public Set<Long> getCommodityIds() {
            return commodityIds;
        }

        public void setCommodityIds(Set<Long> commodityIds) {
            this.commodityIds = commodityIds;
        }
    }

    /**
     * Inner class for releasing stock
     */
    class ReleaseStockRequest {
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