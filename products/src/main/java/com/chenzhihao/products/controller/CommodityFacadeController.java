package com.chenzhihao.products.controller;

import cn.hutool.core.bean.BeanUtil;
import com.chenzhihao.api.dto.CommodityDTO;
import com.chenzhihao.products.service.ICommodityService;
import com.chenzhihao.shopcommon.util.UserContext;
import com.chenzhihao.shopcommon.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 商品服务REST API接口
 * 替代原有的Dubbo CommodityFacadeImp实现
 * @author ASUS
 */
@RestController
@RequestMapping("/api/internal/commodities")
public class CommodityFacadeController {

    @Autowired
    private ICommodityService commodityService;

    /**
     * 根据商品id集合返回商品集合信息
     * 替代原有的Dubbo queryCommodityByIds方法
     * @param request 查询商品信息的请求对象
     * @return 商品信息列表
     */
    @PostMapping("/query-by-ids")
    public Result<List<CommodityDTO>> queryCommodityByIds(@RequestBody com.chenzhihao.api.client.CommodityClient.CommodityIdsRequest request) {
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
     * @param request 释放库存的请求对象
     * @return 释放库存操作结果
     */
    @PostMapping("/release-stock")
    public Result<Boolean> releaseStock(@RequestBody com.chenzhihao.api.client.CommodityClient.ReleaseStockRequest request) {
        // Implementation for releasing stock
        // This would typically update the commodity inventory
        System.out.println("Releasing stock for commodity: " + request.getCommodityId() +
                          ", quantity: " + request.getQuantity());
        return Result.success(true);
    }

}