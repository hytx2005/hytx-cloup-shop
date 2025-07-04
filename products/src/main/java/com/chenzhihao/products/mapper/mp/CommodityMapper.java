package com.chenzhihao.products.mapper.mp;

import com.chenzhihao.products.domain.po.Commodity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>
 * 商品模块 Mapper 接口
 * </p>
 *
 * @author hqh
 * @since 2025-06-27
 */
public interface CommodityMapper extends BaseMapper<Commodity> {

    /**
     * 根据商品 ID 列表查询商品信息并加悲观锁
     * @param ids 商品 ID 列表
     * @return 商品信息列表
     */
    List<Commodity> selectCommoditiesForUpdate(@RequestParam("ids") List<Long> ids);


    /**
     * 批量更新商品的已售数量
     * @param  commodities 商品列表
     * @return int
     */
    int batchUpdateSold(@Param("commodities") List<Commodity> commodities);
}
