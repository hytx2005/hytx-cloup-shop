package com.chenzhihao.products.controller;


import com.chenzhihao.products.domain.dto.ComPayDto;
import com.chenzhihao.products.domain.po.Commodity;
import com.chenzhihao.products.domain.vo.ComPayVo;
import com.chenzhihao.products.domain.vo.CommodityRedisVo;
import com.chenzhihao.products.domain.vo.CommoditySearchVo;
import com.chenzhihao.products.service.ICommodityService;
import com.chenzhihao.shopcommon.result.Result;
import com.chenzhihao.shopcommon.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.chenzhihao.products.domain.dto.CommodityQueryDTO;
import com.chenzhihao.products.domain.vo.PageResult;

/**
 * 商品模块Controller
 * 提供商品相关的REST API接口，包括商品查询、搜索和订单创建功能
 *
 * @author hqh
 * @since 2025-06-27
 */
@RestController
@RequestMapping("/commodity")
public class CommodityController {

    private ICommodityService commodityService;
    @Autowired
    public void setCommodityService(ICommodityService commodityService) {
        this.commodityService = commodityService;
    }

    /**
     * 根据ID查询商品信息
     * 从缓存中获取商品信息，缓存未命中则从数据库查询并同步到缓存
     *
     * @param id 商品ID
     * @return 商品信息VO
     */
    @GetMapping("/get/{id}")
    public Result<CommodityRedisVo> getCommodityById(@PathVariable Long id) {
        CommodityRedisVo commodity = commodityService.getCommodityFromCache(id);
        return Result.success(commodity);
    }


    /**
     * 商品搜索接口
     * 根据关键词、价格范围等条件搜索商品，支持排序和分页
     *
     * @param queryDTO 搜索查询DTO，包含关键词、价格范围、排序和分页参数
     * @return 分页查询结果
     */
    @GetMapping("/search")
    public PageResult<CommoditySearchVo> search(CommodityQueryDTO queryDTO) {
        return commodityService.search(queryDTO);
    }


    /**
     * 测试用户ID获取
     * 用于验证经过网关后能否正确获取到当前登录用户的ID
     *
     * @return 当前登录用户的ID
     */
    @GetMapping("/testId")
    public Result<Long> testId() {
        Long id = UserContext.getUserId();
        return Result.success(id);
    }


    /**
     * 根据商品信息创建支付订单
     * 验证库存、生成订单号、创建订单记录并清空购物车
     *
     * @param comPayDto 商品支付信息DTO
     * @return 订单创建结果，包含订单号
     */
    @PostMapping("/pay")
    public Result<ComPayVo> crePay(@RequestBody ComPayDto comPayDto){
       return commodityService.crePay(comPayDto);
    }


}
