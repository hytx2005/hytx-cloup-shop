package com.chenzhihao.products.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {
    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页数量
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 当前页数据列表
     */
    private List<T> records;

}