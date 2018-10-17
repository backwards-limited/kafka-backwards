package com.backwards.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.kafka.clients.producer.ProducerConfig.*;

public class ProducerDemo {
    private static final Logger logger = LoggerFactory.getLogger(ProducerDemo.class);

    public static void main(String[] args) {
        produce(configuration());
    }

    private static Configuration configuration() {
        String bootStrapServers = "localhost:9092";
        String topic = "first_topic";

        return Configuration.apply(topic).add(BOOTSTRAP_SERVERS_CONFIG, bootStrapServers)
                                         .add(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName())
                                         .add(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    }

    private static void produce(Configuration configuration) {
        System.out.println(configuration.toProperties());

        KafkaProducer<String, String> producer = new KafkaProducer<>(configuration.toProperties());

        for (int i = 0; i < 10; i++) {
            String key = "id_" + i;
            String value = "hello world " + i;

            final ProducerRecord<String, String> record = new ProducerRecord<>(configuration.topic(), key, value);

            // The following is asynchronous - if we do not flush or flush and close, then this app can end before data is actually sent.
            producer.send(record, (recordMetadata, e) -> {
                if (e == null) {
                    logger.info(
                        "\nReceived Metadata for '" + record.key() + "'\n" +
                        "- Topic: " + recordMetadata.topic() + "\n" +
                        "- Partition: " + recordMetadata.partition() + "\n" +
                        "- Offset: " + recordMetadata.offset() + "\n" +
                        "- Timestamp: " + recordMetadata.timestamp()
                    );
                } else {
                    logger.error("Error while producing", e);
                }
            });
        }

        // Either flush:
        // producer.flush();
        // Or flush and close:
        producer.close();
    }
}
