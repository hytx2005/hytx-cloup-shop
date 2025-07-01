package com.chenzhihao.products.domain.vo;

import lombok.Data;
import org.dromara.easyes.core.biz.EsPageInfo;

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
    private List<T> list;

    /**
     * 从 Easy-ES 的分页结果直接转换
     */
    public static <T> PageResult<T> of(EsPageInfo<T> esPageInfo) {
        PageResult<T> result = new PageResult<>();
        result.setPageNum(esPageInfo.getPageNum());
        result.setPageSize(esPageInfo.getPageSize());
        result.setTotal(esPageInfo.getTotal());
        result.setPages(esPageInfo.getPages());
        result.setList(esPageInfo.getList());
        return result;
    }
}