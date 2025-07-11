package com.chenzhihao.products.service;

import com.chenzhihao.products.domain.doc.CommodityEsDoc;
import com.chenzhihao.products.domain.dto.ComPayDto;
import com.chenzhihao.products.domain.dto.CommodityQueryDTO;
import com.chenzhihao.products.domain.po.Commodity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chenzhihao.products.domain.vo.ComPayVo;
import com.chenzhihao.products.domain.vo.CommodityRedisVo;
import com.chenzhihao.products.domain.vo.PageResult;
import com.chenzhihao.shopcommon.result.Result;

/**
 * <p>
 * 商品模块 服务类
 * </p>
 *
 * @author hqh
 * @since 2025-06-27
 */
public interface ICommodityService extends IService<Commodity> {

     /**
      * 旁路缓存策略 - 用商品id获取商品信息
      * @param id 商品id
      * @return {@link CommodityRedisVo }
      */
     CommodityRedisVo getCommodityFromCache(Long id);



    PageResult<CommodityEsDoc> search(CommodityQueryDTO dto);

    /**
     * 根据商品信息生成订单
     * @param comPayDto 商品信息
     * @return {@link Result }<{@link ComPayVo }>
     */
    Result<ComPayVo> crePay(ComPayDto comPayDto);
}
