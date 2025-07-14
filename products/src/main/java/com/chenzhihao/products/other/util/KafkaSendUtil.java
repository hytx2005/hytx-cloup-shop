package com.chenzhihao.products.other.util;

import cn.hutool.json.JSONUtil;
import com.chenzhihao.products.domain.po.ComKafka;
import com.chenzhihao.products.domain.po.Commodity;
import com.chenzhihao.products.domain.vo.CommodityRedisVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author kafka工具类
 */
@Component
@Slf4j
public class KafkaSendUtil {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private RedisUtil redisUtil;

    public static final String ADD_REDIS_TOPIC = "add_commodity_to_redis";
    /**
     * 使用消息队列来实现商品信息的缓存，将商品信息存入redis中
     * @param commodity 商品信息
     */
    public void addCommodityToRedis(Commodity commodity) {
        String message = JSONUtil.toJsonStr(commodity);
        log.info("发送消息：{}", message);
        kafkaTemplate.send(ADD_REDIS_TOPIC, message);
    }


    @KafkaListener(topics = ADD_REDIS_TOPIC)
    public void listenAddCommodityToRedis(ConsumerRecord<String,String> record, Acknowledgment ack){
        String value = record.value();
        Commodity commodity = JSONUtil.toBean(value, Commodity.class);
        log.info("kafka监听到商品信息：{}", commodity);
        redisUtil.saveCommodity(commodity);
        ack.acknowledge();
    }










    public static final String UPDATE_ORDER_REDIS = "updateOrderRedis";
    public static final String UPDATE_KAFKA = "updateKafka:";
    public static final Map<String,List<ComKafka>> ORDER_MAP = new HashMap<>();

    private RedissonClient redisson;
    @Autowired
    public void setRedisson(RedissonClient redisson) {
        this.redisson = redisson;
    }

    /**
     * 发送消息，设置超时时间
     * @param comKafka 消息
     */
    public void sendMessage(ComKafka comKafka){
        LocalDateTime createTime = comKafka.getCreateTime();
        if(createTime == null){
            createTime = LocalDateTime.now();
        }
        LocalDateTime localDateTime = createTime.plusMinutes(minute);
        comKafka.setCreateTime(localDateTime);
        String message = JSONUtil.toJsonStr(comKafka);
        kafkaTemplate.send(UPDATE_ORDER_REDIS, message);
    }


    @KafkaListener(topics = UPDATE_ORDER_REDIS)
    public void addOrderToList(ConsumerRecord<String,String> record, Acknowledgment ack){
        String value = record.value();
        ComKafka comKafka = JSONUtil.toBean(value, ComKafka.class);
        String lockKey = UPDATE_KAFKA+comKafka.getOrderNo();
        RLock lock = redisson.getLock(lockKey);
        try {
            // 尝试获取锁，等待 10 秒，锁自动释放时间为 30 秒
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                log.info("----------------kafka监听到订单信息：{}---------------", comKafka);
                if (ORDER_MAP.containsKey(comKafka.getOrderNo())){
                    List<ComKafka> comKafkaList = ORDER_MAP.get(comKafka.getOrderNo());
                    comKafkaList.add(comKafka);
                }else {
                    List<ComKafka> comKafkaList = new ArrayList<>();
                    comKafkaList.add(comKafka);
                    ORDER_MAP.put(comKafka.getOrderNo(), comKafkaList);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 获取分布式锁，从队列中移除订单信息
     * @param orderNo 订单号
     */
    public void removePayOrders(String orderNo){
        if(!ORDER_MAP.containsKey(orderNo)){
            return;
        }
        String lockKey = UPDATE_KAFKA+orderNo;
        RLock lock = redisson.getLock(lockKey);
        try {
            // 尝试获取锁，等待 10 秒，锁自动释放时间为 30 秒
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                ORDER_MAP.remove(orderNo);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }

    @Value("${pay.consumerTime}")
    public Integer minute;

    /**
     * 校验时间，更新redis中的库存信息
     * @param orderNo 订单号
     */
    public void updateRedisFromKafka(String orderNo) {
        List<ComKafka> comKafkaList = ORDER_MAP.get(orderNo);
        if(comKafkaList == null){
            return;
        }
        String lockKey = UPDATE_KAFKA+orderNo;
        RLock lock = redisson.getLock(lockKey);
        try {
            // 尝试获取锁，等待 10 秒，锁自动释放时间为 30 秒
            if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                for (ComKafka comKafka : comKafkaList) {
                    System.out.println(comKafka);
                    LocalDateTime consumerTime = comKafka.getCreateTime();
                    if (consumerTime.isAfter(LocalDateTime.now())){
                        log.info("订单超时，订单号：{}", orderNo);
                        redisUtil.updateComPayNum(comKafka.getComId(), comKafka.getNum());
                        ORDER_MAP.remove(orderNo);
                    }else {
                        log.info("订单未超时，库存不回滚，订单号：{}", orderNo);
                    }
                }
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
