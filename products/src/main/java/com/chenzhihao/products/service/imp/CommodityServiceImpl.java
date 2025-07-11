package com.chenzhihao.products.service.imp;


import com.chenzhihao.products.domain.doc.CommodityEsDoc;
import com.chenzhihao.products.domain.dto.CommodityQueryDTO;
import com.chenzhihao.products.domain.po.Commodity;
import com.chenzhihao.products.domain.vo.CommodityRedisVo;
import com.chenzhihao.products.other.util.KafkaSendUtil;
import com.chenzhihao.products.other.util.RedisUtil;
import com.chenzhihao.products.domain.vo.PageResult;
import com.chenzhihao.products.mapper.es.CommodityEsMapper;
import com.chenzhihao.products.mapper.mp.CommodityMapper;
import com.chenzhihao.products.service.ICommodityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenzhihao.shopcommon.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * <p>
 * 商品模块 服务实现类
 * </p>
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
    private KafkaSendUtil kafkaSendUtil;


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


    }