package com.chenzhihao.products.service.imp;

import com.chenzhihao.products.domain.po.Commodity;
import com.chenzhihao.products.mapper.CommodityMapper;
import com.chenzhihao.products.service.ICommodityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
