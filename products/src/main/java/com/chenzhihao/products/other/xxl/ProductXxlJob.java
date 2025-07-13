package com.chenzhihao.products.other.xxl;

import com.chenzhihao.products.domain.vo.CommodityRedisVo;
import com.chenzhihao.products.other.util.RedisUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 商品模块的定时任务
 * @author ASUS
 */
@Component
@Slf4j
public class ProductXxlJob {

    @Autowired
    RedisUtil redisUtil;
    /**
     * 定时更新redis数据到mysql
     */
    @XxlJob("updateRedisToMysql")
    public void updateRedisToMysql() {
        // 分片参数
        int index = XxlJobHelper.getShardIndex();
        log.info("分片参数为{}",index);
        int total = XxlJobHelper.getShardTotal();
        log.info("分片总数为{}",total);
        Map<Long, CommodityRedisVo> commodityMap = redisUtil.getCommodityMap();

        for (Long l : commodityMap.keySet()) {
            log.info("商品id{}",l);
            if (l % total == index){
                CommodityRedisVo commodityRedisVo = commodityMap.get(l);
                if (!commodityRedisVo.getVersion().equals(0)){
                    log.info("商品id为{}的商品库存发生变化，更新数据至mysql",l);
                    redisUtil.updateCommodityToMysql(commodityRedisVo);
                }
            }
        }
        log.info("数据更新完毕");
    }
}
