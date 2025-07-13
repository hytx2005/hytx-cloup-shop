package com.chenzhihao.products.other.util;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chenzhihao.products.domain.dto.PayDetail;
import com.chenzhihao.products.domain.po.Commodity;
import com.chenzhihao.products.domain.vo.CommodityRedisVo;
import com.chenzhihao.products.mapper.mp.CommodityMapper;
import lombok.Data;
import org.redisson.api.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * redis工具类
 * @author dhx
 */
@Component
@Data
public class RedisUtil {


    @Autowired
    RedissonClient redisson;
    @Autowired
    CommodityMapper commodityMapper;

    public static final String COMMODITY_HASH_KEY = "payCommodity";



    /**
     * 将商品信息存入redis中
     * 这里使用的是hash结构，key为commodity，value为一个map，map的key为商品id，value为商品信息
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
     * 从redis中获取商品信息
     * @param id 商品id
     * @return {@link Commodity }
     */
    public CommodityRedisVo getCommodity(Long id){
        RMap<Long, CommodityRedisVo> map = redisson.getMap(COMMODITY_HASH_KEY);
        return map.get(id);
    }

    /**
     * 删除商品信息
     * @param id 商品id
     */
    public void deleteCommodity(Long id) {
        RMap<Long, CommodityRedisVo> map = redisson.getMap(COMMODITY_HASH_KEY);
        map.remove(id);
    }



    public static final String LOCK_KEY = "lock_commodity:";

    /**
     * 检查库存并更新 payNum 和 version 字段
     * @param id 商品 ID
     * @param addPayNum 需要增加到 payNum 的值
     * @return 库存充足并更新成功返回 true，否则返回 false
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
     * 这里使用的是hash结构，key为commodity，value为一个map，map的key为商品id，value为商品信息
     * @param maps 商品id和购买的数量 的集合
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
     * @param details 商品信息
     * @return boolean
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
     * 获取redis中存储的所有商品信息
     * @return {@link Map }<{@link Long },{@link CommodityRedisVo }>
     */
    public Map<Long,CommodityRedisVo> getCommodityMap(){
        return redisson.getMap(COMMODITY_HASH_KEY);
    }


    /**
     * 更新redis商品信息到mysql中
     * @param vo 商品信息
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
}
