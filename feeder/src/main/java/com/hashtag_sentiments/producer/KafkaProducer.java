package com.hashtag_sentiments.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.Future;

public class KafkaProducer<K extends Serializable, V extends Serializable> {

    private org.apache.kafka.clients.producer.KafkaProducer producer;
    private boolean syncSend = true;
    private volatile boolean shutDown = false;


    public KafkaProducer(Properties producerConfig) {
        this.producer = new org.apache.kafka.clients.producer.KafkaProducer(producerConfig);
    }

    public void send(String topic, K key, V value) {
        if (shutDown) {
            throw new RuntimeException("Producer is closed.");
        }

        try {
            ProducerRecord record;
            record = new ProducerRecord<>(topic, key, value);
            Future<RecordMetadata> future = producer.send(record);
            if (!syncSend) return;
            future.get();
        } catch (Exception e) {
            System.out.println("Error while producing event for topic : {}" + topic + e);
        }

    }


    public void close() {
        shutDown = true;
        try {
            producer.close();
        } catch (Exception e) {
            System.out.println("Exception occurred while stopping the producer" + e);
        }
    }
}