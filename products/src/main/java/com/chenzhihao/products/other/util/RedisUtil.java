package com.chenzhihao.products.other.util;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chenzhihao.products.domain.dto.PayDetail;
import com.chenzhihao.products.domain.po.Commodity;
import com.chenzhihao.products.domain.vo.CommodityRedisVo;
import com.chenzhihao.products.mapper.mp.CommodityMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 *
 * 提供商品库存管理的Redis操作
 *
 * 数据结构：
 * - Hash：存储商品信息（payCommodity）
 *   - Key: 商品ID
 *   - Value: 商品Redis视图对象（含库存、已售、锁定库存、版本号）
 * - 分布式锁：防止并发修改导致库存超卖
 *
 * 库存管理机制：
 * - stock: 总库存（不变）
 * - sold: 已售数量（累计）
 * - payNum: 锁定库存（支付中）
 * - version: 版本号（用于标识数据是否变更）
 *
 * 可用库存 = stock - sold - payNum
 *
 * @author dhx
 */
@Component
@Data
@Slf4j
public class RedisUtil {


    @Autowired
    RedissonClient redisson;
    @Autowired
    CommodityMapper commodityMapper;

    /**
     * 商品Hash键名
     */
    public static final String COMMODITY_HASH_KEY = "payCommodity";



    /**
     * 将商品信息存入Redis中
     *
     * 这里使用的是hash结构，key为commodity，value为一个map，map的key为商品id，value为商品信息
     *
     * @param commodity 商品信息
     */
    public void saveCommodity(Commodity commodity){
        RMap<Long, CommodityRedisVo> map = redisson.getMap(COMMODITY_HASH_KEY);
        CommodityRedisVo commodityRedisVo = CommodityRedisVo.builder().build();
        BeanUtils.copyProperties(commodity, commodityRedisVo);
        commodityRedisVo.setPayNum(0);
        commodityRedisVo.setVersion(0);
        map.put(commodityRedisVo.getId(), commodityRedisVo);
    }


    /**
     * 从Redis中获取商品信息
     *
     * @param id 商品ID
     * @return 商品Redis视图对象
     */
    public CommodityRedisVo getCommodity(Long id){
        RMap<Long, CommodityRedisVo> map = redisson.getMap(COMMODITY_HASH_KEY);
        return map.get(id);
    }

    /**
     * 删除商品信息
     *
     * @param id 商品ID
     */
    public void deleteCommodity(Long id) {
        RMap<Long, CommodityRedisVo> map = redisson.getMap(COMMODITY_HASH_KEY);
        map.remove(id);
    }



    public static final String LOCK_KEY = "lock_commodity:";

    /**
     * 检查库存并更新payNum和version字段
     *
     * 使用分布式锁保证原子性，防止库存超卖
     *
     * @param id 商品ID
     * @param addPayNum 需要增加到payNum的值
     * @return 库存充足并更新成功返回true，否则返回false
     */
    private boolean checkStockAndUpdate(Long id, Integer addPayNum) {
        String lockKey = LOCK_KEY + id;
        RLock lock = redisson.getLock(lockKey);
        try {
            // 尝试获取锁，等待 10 秒，锁自动释放时间为 30 秒
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                RMap<Long, CommodityRedisVo> map = redisson.getMap(COMMODITY_HASH_KEY);
                CommodityRedisVo vo = map.get(id);

                int stock = vo.getStock();
                int sold = vo.getSold();
                int payNum = vo.getPayNum();
                int version = vo.getVersion();

                int availableStock = stock - sold - payNum;
                if (availableStock >= addPayNum) {
                    payNum += addPayNum;
                    version++;
                    vo.setVersion(version);
                    vo.setPayNum(payNum);
                    map.put(id, vo);
                    return true;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return false;
    }


    /**
     * 库存回滚
     *
     * 减少对应商品的payNum和version
     *
     * @param maps 商品ID和需回滚数量的映射
     */
    private void rollbackStockAndVersion(Map<Long, Integer> maps) {
        for (Long id : maps.keySet()) {
            String lockKey = LOCK_KEY + id;
            RLock lock = redisson.getLock(lockKey);
            try {
                if (lock.tryLock(10,30,TimeUnit.SECONDS)){
                    RMap<Long, CommodityRedisVo> map = redisson.getMap(COMMODITY_HASH_KEY);
                    CommodityRedisVo commodityRedisVo = map.get(id);
                    commodityRedisVo.setPayNum(commodityRedisVo.getPayNum() - maps.get(id));
                    commodityRedisVo.setVersion(commodityRedisVo.getVersion() - 1);
                    map.put(id, commodityRedisVo);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }finally {
                if (lock.isHeldByCurrentThread()){
                    lock.unlock();
                }
            }
        }
    }


    /**
     * 批量检查库存并更新
     *
     * 原子性地检查多个商品的库存并锁定
     *
     * 实现机制：
     * 1. 对商品ID排序，避免死锁
     * 2. 依次获取分布式锁
     * 3. 检查库存是否充足
     * 4. 增加payNum锁定库存
     * 5. 任一商品库存不足则回滚所有已锁定的库存
     *
     * 防死锁策略：
     * - 所有请求按相同顺序获取锁
     * - 使用tryLock避免长时间阻塞
     *
     * @param details 商品购买明细列表
     * @return 所有商品库存充足返回true，否则返回false
     */
    public boolean batchCheckCom(List<PayDetail> details){
        // 对details进行排序
       details.sort(new Comparator<PayDetail>() {
           @Override
           public int compare(PayDetail o1, PayDetail o2) {
               return o1.getCommodityId().compareTo(o2.getCommodityId());
           }
       });

       Map<Long, Integer> success = new HashMap<>();
        for (PayDetail detail : details) {
            Long id = detail.getCommodityId();
            Integer num = detail.getNum();
            if (checkStockAndUpdate(id, num)){
                success.put(id, num);
            }else {
                rollbackStockAndVersion(success);
                return false;
            }
        }
        return true;
    }


    /**
     * 获取Redis中存储的所有商品信息
     *
     * @return 商品ID到商品Redis视图对象的映射
     */
    public Map<Long,CommodityRedisVo> getCommodityMap(){
        return redisson.getMap(COMMODITY_HASH_KEY);
    }


    /**
     * 更新Redis商品信息到MySQL
     *
     * 将Redis中的商品变更同步到数据库
     *
     * 同步策略：
     * 1. 只同步version!=0的商品（有变更的）
     * 2. 更新完成后将version重置为0
     * 3. 使用分布式锁保证并发安全
     *
     * @param vo 商品Redis视图对象
     */
    public void updateCommodityToMysql(CommodityRedisVo vo){
        String lockKey = LOCK_KEY + vo.getId();
        RLock lock = redisson.getLock(lockKey);
        try {
            if (lock.tryLock(10,30,TimeUnit.SECONDS)){
                Commodity commodity = Commodity.builder()
                        .id(vo.getId())
                        .sold(vo.getSold())
                        .build();
                UpdateWrapper<Commodity> wrapper = new UpdateWrapper<>();
                wrapper.eq("id",commodity.getId());
                commodityMapper.update(commodity,wrapper);

                // 更新redis中的version字段，标记redis中的数据尚未更改
                RMap<Long, CommodityRedisVo> map = redisson.getMap(COMMODITY_HASH_KEY);
                CommodityRedisVo commodityRedisVo = map.get(vo.getId());
                commodityRedisVo.setVersion(0);
                map.put(vo.getId(), commodityRedisVo);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }


    /**
     * 订单未支付，回滚商品的锁定库存
     *
     * 减少商品的payNum，释放已锁定的库存
     *
     * @param comId 商品ID
     * @param dec 需要回滚的数量
     */
    public void updateComPayNum(Long comId,Integer dec){
        String lockKey = LOCK_KEY + comId;
        RLock lock = redisson.getLock(lockKey);
        try {
            // 尝试获取锁，等待 10 秒，锁自动释放时间为 30 秒
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                RMap<Long, CommodityRedisVo> map = redisson.getMap(COMMODITY_HASH_KEY);
                CommodityRedisVo vo = map.get(comId);
                log.info("回滚商品库存，商品为{}，回滚数量为{}",vo,dec);
                if (vo == null){
                    return;
                }
                Integer payNum = vo.getPayNum();
                payNum -= dec;
                vo.setPayNum(payNum);
                map.put(comId, vo);

                RMap<Long, CommodityRedisVo> map1 = redisson.getMap(COMMODITY_HASH_KEY);
                CommodityRedisVo vo1 = map1.get(comId);
                log.info("回滚后商品数据为: {}",vo1);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
