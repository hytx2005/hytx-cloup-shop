package com.chenzhihao.products.other.util;

import com.chenzhihao.products.domain.po.Commodity;
import lombok.Data;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

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

    public static final String COMMODITY_HASH_KEY = "commodity";

    private static final long CACHE_TIME = 30;

    private static final TimeUnit CACHE_TIME_UNIT = TimeUnit.MINUTES;

    /**
     * 将商品信息存入redis中
     * 这里使用的是hash结构，key为commodity，value为一个map，map的key为商品id，value为商品信息
     * @param commodity 商品信息
     */
    public void saveCommodity(Commodity commodity) {
        RMapCache<String, Commodity> map = redisson.getMapCache(COMMODITY_HASH_KEY);
        map.put(commodity.getId().toString(), commodity,CACHE_TIME,CACHE_TIME_UNIT);
    }


    /**
     * 从redis中获取商品信息
     * @param id 商品id
     * @return {@link Commodity }
     */
    public Commodity getCommodity(Long id) {
        RMapCache<String, Commodity> map = redisson.getMapCache(COMMODITY_HASH_KEY);
        return map.get(id.toString());
    }

    /**
     * 删除商品信息
     * @param id 商品id
     */
    public void deleteCommodity(Long id) {
        RMap<String, Commodity> map = redisson.getMap(COMMODITY_HASH_KEY);
        map.remove(id.toString());
    }

}
