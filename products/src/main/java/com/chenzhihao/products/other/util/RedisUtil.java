package com.chenzhihao.products.other.util;

import com.chenzhihao.products.domain.po.Commodity;
import com.chenzhihao.products.domain.vo.CommodityRedisVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将商品信息存入redis中
     * 这里使用的是hash结构，key为commodity，value为一个map，map的key为商品id，value为商品信息
     * @param commodity 商品信息
     */
    public void saveCommodity(Commodity commodity){
        RMapCache<String, String> map = redisson.getMapCache(COMMODITY_HASH_KEY);
        CommodityRedisVo commodityRedisVo = CommodityRedisVo.builder().build();
        BeanUtils.copyProperties(commodity, commodityRedisVo);
        commodityRedisVo.setPayNum(0);
        String json = null;
        try {
            json = objectMapper.writeValueAsString(commodityRedisVo);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        map.put(commodityRedisVo.getId().toString(), json,CACHE_TIME,CACHE_TIME_UNIT);
    }


    /**
     * 从redis中获取商品信息
     * @param id 商品id
     * @return {@link Commodity }
     */
    public CommodityRedisVo getCommodity(Long id){
        RMapCache<String, String> map = redisson.getMapCache(COMMODITY_HASH_KEY);
        String json =  map.get(id.toString());
        if (json != null) {
            try {
                return objectMapper.readValue(json, CommodityRedisVo.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    /**
     * 删除商品信息
     * @param id 商品id
     */
    public void deleteCommodity(Long id) {
        RMap<String, CommodityRedisVo> map = redisson.getMap(COMMODITY_HASH_KEY);
        map.remove(id.toString());
    }

}
