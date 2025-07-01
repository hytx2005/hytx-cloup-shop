package com.chenzhihao.products.other.util;

import cn.hutool.json.JSONUtil;
import com.chenzhihao.products.domain.po.Commodity;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

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

}
