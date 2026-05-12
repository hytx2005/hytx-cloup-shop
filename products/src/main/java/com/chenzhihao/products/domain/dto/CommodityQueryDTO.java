package com.chenzhihao.products.domain.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CommodityQueryDTO {
    /**
     * 搜索关键词
     * 将用于匹配商品名称和规格
     */
    private String keyword;

    /**
     * 最低价格
     * 用于价格范围查询的下限
     */
    private BigDecimal minPrice;

    /**
     * 最高价格
     * 用于价格范围查询的上限
     */
    private BigDecimal maxPrice;

    /**
     * 排序字段
     * 可选值：price-价格，sold-销量
     * 默认为null，表示按ES相关性得分排序
     */
    private String sortField;

    /**
     * 排序方式
     * 可选值：asc-升序，desc-降序
     * 默认为desc
     */
    private String sortOrder = "desc";

    /**
     * 当前页码
     * 默认为1
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     * 默认为10
     */
    private Integer pageSize = 10;
}