package com.backwards.kafka;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static java.util.Collections.singletonList;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;

/**
 * Assign and seek are mostly used to replay data or fetch a specific message.
 * A consumer is not configured with a group (id) and does not "subscribe".
 */
public class ConsumerAssignAndSeekDemo {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerAssignAndSeekDemo.class);

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1);
        final Consumer consumer = consume(latch, configuration());

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::shutdown));

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("Application interrupted", e);
        } finally {
            logger.info("Application closing");
        }
    }

    private static Configuration configuration() {
        String bootStrapServers = "localhost:9092";
        String topic = "first_topic";

        return Configuration.apply(topic).add(BOOTSTRAP_SERVERS_CONFIG, bootStrapServers)
                                         .add(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName())
                                         .add(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName())
                                         .add(AUTO_OFFSET_RESET_CONFIG, "latest");
    }

    private static Consumer consume(CountDownLatch latch, Configuration configuration) {
        return new Consumer(latch, configuration);
    }

    static class Consumer implements Runnable {
        private final int requiredNumberOfMessages = 5;
        private final CountDownLatch latch;
        private final KafkaConsumer<String, String> consumer;

        Consumer(CountDownLatch latch, Configuration configuration) {
            this.latch = latch;
            consumer = new KafkaConsumer<>(configuration.toProperties());

            // Assign
            TopicPartition topicPartition = new TopicPartition(configuration.topic(), 0);
            consumer.assign(singletonList(topicPartition));

            // Seek
            long offset = 15;
            consumer.seek(topicPartition, offset);

            new Thread(this).start();
        }

        @Override
        public void run() {
            int numberOfMessages = 0;

            try {
                while (latch.getCount() > 0) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

                    for (ConsumerRecord<String, String> record : records) {
                        logger.info(
                            "\nReceived record:\n" +
                            "- Key: " + record.key() + "\n" +
                            "- Value: " + record.value() + "\n" +
                            "- Partition: " + record.partition() + "\n" +
                            "- Offset: " + record.offset() + "\n" +
                            "- Timestamp: " + record.timestamp()
                        );

                        if (++numberOfMessages >= requiredNumberOfMessages) {
                            while(latch.getCount() > 0)
                                latch.countDown();

                            break;
                        }
                    }
                }

                logger.info("Ceased polling upon reaching end condition");

            } catch (WakeupException e) {
                logger.info("Received shutdown hook");
            } finally {
                consumer.close();
                latch.countDown();
            }
        }

        void shutdown() {
            consumer.wakeup();

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
