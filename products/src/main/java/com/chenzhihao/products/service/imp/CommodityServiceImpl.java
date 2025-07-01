package com.chenzhihao.products.service.imp;

import cn.hutool.core.util.StrUtil;
import com.chenzhihao.products.domain.doc.CommodityEsDoc;
import com.chenzhihao.products.domain.dto.CommodityQueryDTO;
import com.chenzhihao.products.domain.po.Commodity;
import com.chenzhihao.products.domain.vo.PageResult;
import com.chenzhihao.products.mapper.es.CommodityEsMapper;
import com.chenzhihao.products.mapper.mp.CommodityMapper;
import com.chenzhihao.products.service.ICommodityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CommodityServiceImpl extends ServiceImpl<CommodityMapper, Commodity> implements ICommodityService {

    @Autowired
    private CommodityEsMapper commodityEsMapper;

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