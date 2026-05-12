package com.chenzhihao.products.mapper.mp;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chenzhihao.products.domain.po.Commodity;
import com.chenzhihao.products.domain.vo.CommoditySearchVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品搜索Mapper - 使用MySQL实现商品搜索功能
 */
public interface CommoditySearchMapper extends BaseMapper<Commodity> {

    /**
     * 根据关键词搜索商品（使用LIKE查询）
     * @param keyword 搜索关键词
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param sortField 排序字段
     * @param sortOrder 排序顺序
     * @param offset 分页偏移量
     * @param pageSize 每页数量
     * @return 商品列表
     */
    List<CommoditySearchVo> searchCommodities(
            @Param("keyword") String keyword,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    /**
     * 统计搜索结果总数
     * @param keyword 搜索关键词
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 结果总数
     */
    int countSearchResults(
            @Param("keyword") String keyword,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

}