package com.starter.kafka;

import com.starter.common.DockerComposeExtension;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
@ExtendWith(DockerComposeExtension.class)
public class KafkaTest {
  private static final String MY_TOPIC = "myTopic";
  private static KafkaProducer<Integer, String> producer;
  private static KafkaConsumer<Integer, String> consumer;

  @BeforeAll
  public static void setUp() {
    Properties producerConfig = new Properties();
    producerConfig.put("bootstrap.servers", DockerComposeExtension.kafkaHostPort());
    producerConfig.put("acks", "all");
    producerConfig.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
    producerConfig.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    producer = new KafkaProducer<>(producerConfig);

    Properties consumerConfig = new Properties();
    consumerConfig.put("bootstrap.servers", DockerComposeExtension.kafkaHostPort());
    consumerConfig.put("group.id", "test");
    consumerConfig.put("enable.auto.commit", "true");
    consumerConfig.put("auto.commit.interval.ms", "1000");
    consumerConfig.put("session.timeout.ms", "30000");
    consumerConfig.put("key.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer");
    consumerConfig.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

    consumer = new KafkaConsumer<>(consumerConfig);

    consumer.subscribe(Collections.singleton(MY_TOPIC));
  }

  @Test
  public void test() {
    int key = 1234;
    String message = "A Random Message";
    ProducerRecord<Integer, String> producerRecord = new ProducerRecord<>(MY_TOPIC, key, message);
    producer.send(producerRecord, (metadata, exception) -> {
      if (exception != null) {
        exception.printStackTrace();
      }
      System.out.println("Message Successfully sent");
    });

    ConsumerRecords<Integer, String> records=null;
    while(records==null || records.isEmpty()) {
      System.out.println("Polling records.");
      records = consumer.poll(Duration.ofSeconds(2L));
    }
    consumer.commitSync();
    Assertions.assertEquals(1, records.count());
    ConsumerRecord<Integer, String> record = records.iterator().next();
    Assertions.assertEquals(key, record.key());
    Assertions.assertEquals(message, record.value());
  }
}
