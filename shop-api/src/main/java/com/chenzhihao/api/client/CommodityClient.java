package com.chenzhihao.api.client;

import com.chenzhihao.api.dto.CommodityDTO;
import com.chenzhihao.shopcommon.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Set;

/**
 * 商品服务OpenFeign客户端接口
 * 用于其他服务调用商品服务的功能
 */
@FeignClient(name = "products-service")
public interface CommodityClient {

    /**
     * 根据商品ID集合查询商品信息
     * @param request 包含商品ID集合的请求对象
     * @return 商品信息列表
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/internal/commodities/query-by-ids")
    Result<List<CommodityDTO>> queryCommodityByIds(@RequestBody CommodityIdsRequest request);

    /**
     * 释放商品库存
     * @param request 包含商品ID和数量的请求对象
     * @return 释放库存操作的结果
     */
    @RequestMapping(method = RequestMethod.POST, value = "/api/internal/commodities/release-stock")
    Result<Boolean> releaseStock(@RequestBody ReleaseStockRequest request);

    /**
     * 查询商品信息的请求参数封装类
     */
    class CommodityIdsRequest {
        /**
         * 商品ID集合
         */
        private Set<Long> commodityIds;

        public Set<Long> getCommodityIds() {
            return commodityIds;
        }

        public void setCommodityIds(Set<Long> commodityIds) {
            this.commodityIds = commodityIds;
        }
    }

    /**
     * 释放库存的请求参数封装类
     */
    class ReleaseStockRequest {
        /**
         * 商品ID
         */
        private Long commodityId;

        /**
         * 释放数量
         */
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