package com.chenzhihao.products.controller;


import com.chenzhihao.products.domain.po.Commodity;
import com.chenzhihao.products.service.ICommodityService;
import com.chenzhihao.shopcommon.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.chenzhihao.products.domain.doc.CommodityEsDoc;
import com.chenzhihao.products.domain.dto.CommodityQueryDTO;
import com.chenzhihao.products.domain.vo.PageResult;
import com.chenzhihao.products.mapper.es.CommodityEsMapper;
import com.chenzhihao.products.service.ICommodityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

import javax.annotation.Resource;


/**
 * <p>
 * 商品模块 前端控制器
 * </p>
 *
 * @author hqh
 * @since 2025-06-27
 */
@RestController
@RequestMapping("/commodity")
public class CommodityController {

    @Autowired
    private ICommodityService commodityService;

    @GetMapping("/get/{id}")
    public Result<Commodity> getCommodityById(@PathVariable Long id) {
        Commodity commodity = commodityService.getCommodityFromCache(id);
        return Result.success(commodity);
    }


    /**
     * 商品搜索接口
     * @param queryDTO SpringBoot会自动将URL中的查询参数封装到DTO对象中
     * @return 统一的分页查询结果
     */
    @GetMapping("/search")
    public PageResult<CommodityEsDoc> search(CommodityQueryDTO queryDTO) {
        return commodityService.search(queryDTO);
    }


}
