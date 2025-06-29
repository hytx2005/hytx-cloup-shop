package com.chenzhihao.products.kafkatest;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * @author ASUS
 */
@Component
public class KafkaService {

    @KafkaListener(topics = "topic",groupId = "default-group")
    public void listen(ConsumerRecord<String,String> record, Acknowledgment ack){
        String value = record.value();
        System.out.println("value="+value);
        ack.acknowledge();
    }
}
