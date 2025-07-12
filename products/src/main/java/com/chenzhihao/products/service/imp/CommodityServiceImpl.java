package com.chenzhihao.products.service.imp;


import com.chenzhihao.api.dto.OrderForPay;
import com.chenzhihao.api.facade.CartFacade;
import com.chenzhihao.api.facade.OrderFacade;
import com.chenzhihao.products.domain.doc.CommodityEsDoc;
import com.chenzhihao.products.domain.dto.ComPayDto;
import com.chenzhihao.products.domain.dto.CommodityQueryDTO;
import com.chenzhihao.products.domain.dto.PayDetail;
import com.chenzhihao.products.domain.po.Commodity;
import com.chenzhihao.products.domain.vo.ComPayVo;
import com.chenzhihao.products.domain.vo.CommodityRedisVo;
import com.chenzhihao.products.other.util.KafkaSendUtil;
import com.chenzhihao.products.other.util.RedisUtil;
import com.chenzhihao.products.domain.vo.PageResult;
import com.chenzhihao.products.mapper.es.CommodityEsMapper;
import com.chenzhihao.products.mapper.mp.CommodityMapper;
import com.chenzhihao.products.service.ICommodityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenzhihao.shopcommon.annotation.DubboException;
import com.chenzhihao.shopcommon.exception.BaseException;
import com.chenzhihao.shopcommon.result.Result;
import com.chenzhihao.shopcommon.util.OrderNoUtil;
import com.chenzhihao.shopcommon.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品模块 服务实现类
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
    private KafkaSendUtil kafkaSendUtil;
    @DubboReference
    private OrderFacade orderFacade;
    @DubboReference
    private CartFacade cartFacade;


    /**
     * 旁路缓存策略 - 用商品id获取商品信息
     * 未获取到商品信息时，使用kafka消息队列来推送商品信息到redis中
     * @param id 商品id
     * @return {@link CommodityRedisVo }
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

        // 查找到商品信息，推送到消息队列
        if (commodity!= null) {
            kafkaSendUtil.addCommodityToRedis(commodity);
        }
        // 商品不存在
        else {
            throw new BaseException("商品信息不存在");
        }
        commodityVo = CommodityRedisVo.builder().build();
        BeanUtils.copyProperties(commodity, commodityVo);
        commodityVo.setPayNum(0);
        commodityVo.setVersion(0);
        return commodityVo;
    }

    @Resource
    private CommodityEsMapper commodityEsMapper;

    @Override
    public PageResult<CommodityEsDoc> search(CommodityQueryDTO queryDTO) {
            // 1. 创建查询条件构造器
            LambdaEsQueryWrapper<CommodityEsDoc> wrapper = new LambdaEsQueryWrapper<>();

            // 2. 构建关键词查询
            if (StringUtils.hasText(queryDTO.getKeyword())) {
                wrapper.and(i -> i.match(CommodityEsDoc::getName, queryDTO.getKeyword())
                        .or()
                        .match(CommodityEsDoc::getSpec, queryDTO.getKeyword()));
            }

            // 3. 构建价格范围查询
            if (queryDTO.getMinPrice() != null) {
                wrapper.ge(CommodityEsDoc::getPrice, queryDTO.getMinPrice());
            }
            if (queryDTO.getMaxPrice() != null) {
                wrapper.le(CommodityEsDoc::getPrice, queryDTO.getMaxPrice());
            }

            // 4. 构建动态排序
            if (StringUtils.hasText(queryDTO.getSortField())) {
                boolean isAsc = "asc".equalsIgnoreCase(queryDTO.getSortOrder());
                if ("price".equals(queryDTO.getSortField())) {
                    wrapper.orderBy(true, isAsc, CommodityEsDoc::getPrice);
                } else if ("sold".equals(queryDTO.getSortField())) {
                    wrapper.orderBy(true, isAsc, CommodityEsDoc::getSold);
                }
            }

            // 5. 执行分页查询, 返回结果
            EsPageInfo<CommodityEsDoc> esPageInfo = commodityEsMapper.pageQuery(
                    wrapper, queryDTO.getPageNum(), queryDTO.getPageSize());

            // 6. 转换为统一的返回格式
            return PageResult.of(esPageInfo);
        }


    /**
     * 创建支付订单
     * @param comPayDto 商品信息
     * @return {@link Result }<{@link ComPayVo }>
     */
    @Override
    @DubboException
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

        // 4.调用订单dubbo服务，生成订单数据
        orderFacade.createOrder(forRedis,orderNo);


        // 5.调用购物车服务，删除数据
        List<Long> ids = new ArrayList<>();
        for (PayDetail payDetail : comPayDto.getPayDetails()) {
            ids.add(payDetail.getCommodityId());
        }
        cartFacade.deleteCart(ids);
        // TODO kafka服务
        ComPayVo comPayVo = new ComPayVo();
        comPayVo.setOrderNo(orderNo);
        return Result.success(comPayVo);
    }


    /**
     *  获取对应商品信息，保证商品信息存储在redis中
     * @param details 商品信息
     * @return {@link List }<{@link OrderForPay }>
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


}