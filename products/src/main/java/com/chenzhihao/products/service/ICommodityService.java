package com.chenzhihao.products.service;

import com.chenzhihao.products.domain.doc.CommodityEsDoc;
import com.chenzhihao.products.domain.dto.CommodityQueryDTO;
import com.chenzhihao.products.domain.po.Commodity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chenzhihao.products.domain.vo.PageResult;

/**
 * <p>
 * 商品模块 服务类
 * </p>
 *
 * @author hqh
 * @since 2025-06-27
 */
public interface ICommodityService extends IService<Commodity> {

    PageResult<CommodityEsDoc> search(CommodityQueryDTO dto);
}
