package com.chenzhihao.api.client;

import com.chenzhihao.shopcommon.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 购物车服务OpenFeign客户端接口
 * 用于其他服务通过OpenFeign调用购物车服务的功能
 *
 * @author hqh
 */
@FeignClient(name = "carts-service")
public interface CartClient {

    /**
     * 从购物车中删除商品
     * 根据商品ID列表批量删除购物车中的商品
     *
     * @param request 删除购物车商品的请求对象，包含商品ID列表
     * @return 删除操作结果
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/api/internal/carts/delete")
    Result<Boolean> deleteCart(@RequestBody DeleteCartRequest request);

    /**
     * 删除购物车商品的请求参数封装类
     */
    class DeleteCartRequest {
        /**
         * 要删除的商品ID列表
         */
        private List<Long> commodityIds;

        public List<Long> getCommodityIds() {
            return commodityIds;
        }

        public void setCommodityIds(List<Long> commodityIds) {
            this.commodityIds = commodityIds;
        }
    }
}