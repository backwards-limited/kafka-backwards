package com.backwards.kafka;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import scala.collection.immutable.Seq;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.backwards.config.BootstrapConfig;
import io.lemonlabs.uri.Uri;
import io.lemonlabs.uri.Uri$;
import static java.util.Collections.singletonList;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static scala.jdk.CollectionConverters.ListHasAsScala;

/**
 * Assign and seek are mostly used to replay data or fetch a specific message.
 * A consumer is not configured with a group (id) and does not "subscribe".
 */
public class ConsumerAssignAndSeekDemo {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerAssignAndSeekDemo.class);

    private static final String topic = "first_topic";

    public static void main(String[] args) throws URISyntaxException {
        CountDownLatch latch = new CountDownLatch(1);
        final Consumer consumer = consume(config(), latch);

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::shutdown));

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("Application interrupted", e);
        } finally {
            logger.info("Application closing");
        }
    }

    private static KafkaConfig config() throws URISyntaxException {
        Seq<Uri> bootstrapServers = ListHasAsScala(singletonList(Uri$.MODULE$.apply(new URI("http://127.0.0.1:9092")))).asScala().toSeq();

        return KafkaConfig.apply(new BootstrapConfig(bootstrapServers))
                .add(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName())
                .add(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName())
                .add(AUTO_OFFSET_RESET_CONFIG, "latest");
    }

    private static Consumer consume(KafkaConfig config, CountDownLatch latch) {
        return new Consumer(config, latch);
    }

    static class Consumer implements Runnable {
        private final CountDownLatch latch;
        private final KafkaConsumer<String, String> consumer;

        Consumer(KafkaConfig config, CountDownLatch latch) {
            this.latch = latch;
            consumer = new KafkaConsumer<>(config.toProperties());

            // Assign
            TopicPartition topicPartition = new TopicPartition(topic, 0);
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

                        int requiredNumberOfMessages = 5;

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