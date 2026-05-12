package com.chenzhihao.products.service.imp;


import com.chenzhihao.api.dto.OrderForPay;
import com.chenzhihao.api.client.CartClient;
import com.chenzhihao.api.client.OrderClient;
import com.chenzhihao.products.domain.dto.ComPayDto;
import com.chenzhihao.products.domain.dto.CommodityQueryDTO;
import com.chenzhihao.products.domain.dto.PayDetail;
import com.chenzhihao.products.domain.po.Commodity;
import com.chenzhihao.products.domain.vo.ComPayVo;
import com.chenzhihao.products.domain.vo.CommodityRedisVo;
import com.chenzhihao.products.other.util.RedisUtil;
import com.chenzhihao.products.other.util.OrderTimeoutUtil;
import com.chenzhihao.products.domain.vo.PageResult;
import com.chenzhihao.products.domain.vo.CommoditySearchVo;
import com.chenzhihao.products.mapper.mp.CommoditySearchMapper;
import com.chenzhihao.products.mapper.mp.CommodityMapper;
import com.chenzhihao.products.service.ICommodityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenzhihao.shopcommon.exception.BaseException;
import com.chenzhihao.shopcommon.result.Result;
import com.chenzhihao.shopcommon.util.OrderNoUtil;
import com.chenzhihao.shopcommon.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品模块Service实现类
 * 实现商品相关的业务逻辑，包括缓存管理、搜索、订单创建等功能
 *
 * @author hqh
 * @since 2025-06-27
 */
@Service
@Slf4j
public class CommodityServiceImpl extends ServiceImpl<CommodityMapper, Commodity> implements ICommodityService {

    @Autowired
    private  RedisUtil redisUtil;
    @Autowired
    private CommodityMapper commodityMapper;
    @Autowired
    private OrderClient orderClient;
    @Autowired
    private CartClient cartClient;


    /**
     * 旁路缓存策略 - 根据商品ID获取商品信息
     * 先从缓存查询，缓存未命中则从数据库查询并同步到缓存
     *
     * @param id 商品ID
     * @return 商品信息VO
     */
    @Override
    public CommodityRedisVo getCommodityFromCache(Long id) {
        // 先从redis缓存中查找商品信息
        CommodityRedisVo commodityVo = redisUtil.getCommodity(id);

        // 缓存命中，直接返回
        if (commodityVo != null) {
            log.info("缓存命中，商品信息：{}", commodityVo);
            return commodityVo;
        }
        // 缓存未命中，从数据库中查找商品信息
        Commodity commodity = commodityMapper.selectById(id);

        // 商品不存在
        if (commodity == null) {
            throw new BaseException("商品信息不存在");
        }

        // 查找到商品信息，直接同步保存到Redis
        redisUtil.saveCommodity(commodity);

        commodityVo = CommodityRedisVo.builder().build();
        BeanUtils.copyProperties(commodity, commodityVo);
        commodityVo.setPayNum(0);
        commodityVo.setVersion(0);
        return commodityVo;
    }

    @Autowired
    private CommoditySearchMapper commoditySearchMapper;

    @Override
    public PageResult<CommoditySearchVo> search(CommodityQueryDTO queryDTO) {
        // 计算分页偏移量
        int offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
        int pageSize = queryDTO.getPageSize();

        // 执行搜索查询
        List<CommoditySearchVo> searchResults = commoditySearchMapper.searchCommodities(
                queryDTO.getKeyword(),
                queryDTO.getMinPrice(),
                queryDTO.getMaxPrice(),
                queryDTO.getSortField(),
                queryDTO.getSortOrder(),
                offset,
                pageSize
        );

        // 统计总数
        int total = commoditySearchMapper.countSearchResults(
                queryDTO.getKeyword(),
                queryDTO.getMinPrice(),
                queryDTO.getMaxPrice()
        );

        // 构建分页结果
        PageResult<CommoditySearchVo> pageResult = new PageResult<>();
        pageResult.setRecords(searchResults);
        pageResult.setTotal((long) total);
        pageResult.setPageSize(pageSize);
        pageResult.setPageNum(queryDTO.getPageNum());
        pageResult.setPages((int) Math.ceil((double) total / pageSize));

        return pageResult;
    }


    /**
     * 创建支付订单
     * 验证库存、生成订单号、调用订单服务创建订单、清空购物车
     *
     * @param comPayDto 商品支付信息DTO
     * @return 支付订单结果，包含订单号
     */
    @Override
    public Result<ComPayVo> crePay(ComPayDto comPayDto) {

        // 1.从redis中获取商品信息，调用service层接口，保证商品信息已经存储在redis
        List<OrderForPay> forRedis = getComForRedis(comPayDto.getPayDetails());
        // 2.去redis校验商品库存是否足够
        boolean b = redisUtil.batchCheckCom(comPayDto.getPayDetails());
        if (!b) {
            throw  new BaseException("商品库存不足");
        }

        // 3.生成订单号
        String orderNo = OrderNoUtil.generateOrderNo();

        // 4.调用订单服务，生成订单数据
        OrderClient.CreateOrderRequest orderRequest = new OrderClient.CreateOrderRequest();
        orderRequest.setOrderNo(orderNo);
        orderRequest.setOrderItems(convertToOrderItems(forRedis));
        orderClient.createOrder(orderRequest);

        // 5.调用购物车服务，删除数据
        List<Long> ids = new ArrayList<>();
        for (PayDetail payDetail : comPayDto.getPayDetails()) {
            ids.add(payDetail.getCommodityId());
            Long commodityId = payDetail.getCommodityId();
            Integer num = payDetail.getNum();
            // 将订单商品信息直接保存到内存中，供XXL-Job定时任务处理超时
            OrderTimeoutUtil.addOrderItem(orderNo, commodityId, num);
        }
        CartClient.DeleteCartRequest cartRequest = new CartClient.DeleteCartRequest();
        cartRequest.setCommodityIds(ids);
        cartClient.deleteCart(cartRequest);
        ComPayVo comPayVo = new ComPayVo();
        comPayVo.setOrderNo(orderNo);
        return Result.success(comPayVo);
    }


    /**
     * 获取商品信息并确保存储在Redis中
     * 用于订单创建时获取商品详情，将商品信息转换为订单信息格式
     *
     * @param details 商品支付详情列表
     * @return 商品订单信息列表
     */
    @Override
    public List<OrderForPay> getComForRedis(List<PayDetail> details) {
        List<OrderForPay> result = new ArrayList<>();
        for (PayDetail detail : details) {
            CommodityRedisVo commodity = getCommodityFromCache(detail.getCommodityId());
            OrderForPay orderForPay = OrderForPay.builder()
                    .id(commodity.getId())
                    .price(commodity.getPrice())
                    .name(commodity.getName())
                    .imageUrl(commodity.getImageUrl())
                    .num(detail.getNum())
                    .build();
            result.add(orderForPay);
        }
        return result;
    }

    /**
     * 转换商品订单信息列表为订单服务需要的格式
     *
     * @param forRedis 商品订单信息列表
     * @return 订单服务需要的订单项列表
     */
    private java.util.List<OrderClient.OrderItem> convertToOrderItems(java.util.List<OrderForPay> forRedis) {
        List<OrderClient.OrderItem> orderItems = new ArrayList<>();
        for (OrderForPay orderForPay : forRedis) {
            OrderClient.OrderItem item = new OrderClient.OrderItem();
            item.setId(orderForPay.getId());
            item.setName(orderForPay.getName());
            item.setImageUrl(orderForPay.getImageUrl());
            item.setNum(orderForPay.getNum());
            item.setPrice(orderForPay.getPrice());
            orderItems.add(item);
        }
        return orderItems;
    }


}