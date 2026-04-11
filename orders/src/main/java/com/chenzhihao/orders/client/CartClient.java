package com.chenzhihao.orders.client;

import com.chenzhihao.shopcommon.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * OpenFeign client for Cart Service (replaces @DubboReference CartFacade)
 * @author dhx
 */
@FeignClient(name = "carts-service")
public interface CartClient {

    /**
     * Delete cart items (equivalent to CartFacade.deleteCart)
     * @param request DeleteCartRequest containing commodity IDs
     * @return Result<Boolean>
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/api/internal/carts/delete")
    Result<Boolean> deleteCart(@RequestBody DeleteCartRequest request);

    /**
     * Inner class to match the request structure
     */
    class DeleteCartRequest {
        private List<Long> commodityIds;

        public List<Long> getCommodityIds() {
            return commodityIds;
        }

        public void setCommodityIds(List<Long> commodityIds) {
            this.commodityIds = commodityIds;
        }
    }
}