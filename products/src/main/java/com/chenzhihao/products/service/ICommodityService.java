package com.chenzhihao.products.service;

import com.chenzhihao.api.dto.OrderForPay;
import com.chenzhihao.products.domain.dto.ComPayDto;
import com.chenzhihao.products.domain.dto.CommodityQueryDTO;
import com.chenzhihao.products.domain.dto.PayDetail;
import com.chenzhihao.products.domain.po.Commodity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chenzhihao.products.domain.vo.ComPayVo;
import com.chenzhihao.products.domain.vo.CommodityRedisVo;
import com.chenzhihao.products.domain.vo.CommoditySearchVo;
import com.chenzhihao.products.domain.vo.PageResult;
import com.chenzhihao.shopcommon.result.Result;

import java.util.List;

/**
 * 商品模块Service接口
 * 提供商品相关的业务逻辑处理，继承MyIService以获得基础CRUD功能
 *
 * @author hqh
 * @since 2025-06-27
 */
public interface ICommodityService extends IService<Commodity> {

     /**
      * 旁路缓存策略 - 根据商品ID获取商品信息
      * 先从缓存查询，缓存未命中则从数据库查询并同步到缓存
      *
      * @param id 商品ID
      * @return 商品信息VO
      */
     CommodityRedisVo getCommodityFromCache(Long id);



    PageResult<CommoditySearchVo> search(CommodityQueryDTO dto);

    /**
     * 根据商品信息创建支付订单
     * 验证库存、生成订单号、创建订单记录并清空购物车
     *
     * @param comPayDto 商品支付信息DTO
     * @return 订单创建结果，包含订单号
     */
    Result<ComPayVo> crePay(ComPayDto comPayDto);


    /**
     * 获取商品信息并确保存储在Redis中
     * 用于订单创建时获取商品详情
     *
     * @param details 商品支付详情列表
     * @return 商品订单信息列表
     */
    List<OrderForPay> getComForRedis(List<PayDetail> details);
}
