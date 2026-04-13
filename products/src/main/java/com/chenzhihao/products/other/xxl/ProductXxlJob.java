package com.chenzhihao.products.other.xxl;

import com.chenzhihao.products.domain.vo.CommodityRedisVo;
import com.chenzhihao.products.other.util.OrderTimeoutUtil;
import com.chenzhihao.products.other.util.RedisUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Map;

/**
 * 商品模块定时任务
 *
 * 使用XXL-Job执行定时任务
 *
 * 任务列表：
 * 1. updateRedisToMysql：同步Redis库存变更到MySQL
 * 2. updateKafka：检查超时订单并回滚库存
 *
 * 分片机制：
 * - 支持多实例并行执行
 * - 根据商品ID/订单号取模分片
 * - 避免重复处理同一数据
 *
 * @author ASUS
 */
@Component
@Slf4j
public class ProductXxlJob {

    @Autowired
    RedisUtil redisUtil;

    /**
     * 定时更新Redis数据到MySQL
     *
     * 将Redis中发生变更的商品库存同步到数据库
     *
     * 执行逻辑：
     * 1. 获取分片参数（index, total）
     * 2. 遍历所有商品
     * 3. 商品ID % total == index 的商品由当前实例处理
     * 4. version!=0表示有变更，同步到MySQL
     * 5. 同步成功后version重置为0
     *
     * 性能优化：
     * - 分片并行处理
     * - 只处理有变更的数据
     */
    @XxlJob("updateRedisToMysql")
    public void updateRedisToMysql() {
        // 分片参数
        int index = XxlJobHelper.getShardIndex();
        int total = XxlJobHelper.getShardTotal();
        log.info("分片参数：index={}, total={}", index, total);
        log.info("时间段：{}", LocalTime.now());
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


    @Autowired
    OrderTimeoutUtil orderTimeoutUtil;

    /**
     * 检查并处理超时订单
     *
     * 扫描内存中的订单，检查是否超时
     *
     * 执行逻辑：
     * 1. 获取分片参数
     * 2. 根据订单号首字符分片
     * 3. 检查订单是否超过30分钟未支付
     * 4. 超时则回滚库存并清理内存
     *
     * 替代方案：
     * - 原使用Kafka处理超时
     * - 现使用内存Map+定时任务
     * - 配合Redisson延迟队列
     */
    @XxlJob("updateKafka")
    public void updateKafka() {
        // 分片参数
        int index = XxlJobHelper.getShardIndex();
        int total = XxlJobHelper.getShardTotal();
        log.info("准备检查超时订单并回滚库存");
        for (String orderNo : OrderTimeoutUtil.ORDER_MAP.keySet()) {
            int hashCode = orderNo.charAt(0);
            if (hashCode % total == index) {
                orderTimeoutUtil.updateRedisFromOrder(orderNo);
            }
        }
    }
}
