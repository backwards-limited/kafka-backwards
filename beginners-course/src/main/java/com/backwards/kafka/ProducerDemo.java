package com.backwards.kafka;

import java.net.URI;
import java.net.URISyntaxException;
import scala.collection.Seq;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.backwards.kafka.config.BootstrapConfig;
import com.backwards.kafka.config.KafkaConfig;
import io.lemonlabs.uri.Uri;
import io.lemonlabs.uri.Uri$;
import static java.util.Collections.singletonList;
import static org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;
import static scala.collection.JavaConverters.asScalaBuffer;

public class ProducerDemo {
    private static final Logger logger = LoggerFactory.getLogger(ProducerDemo.class);

    public static void main(String[] args) throws URISyntaxException {
        produce("first_topic", config());
    }

    private static KafkaConfig config() throws URISyntaxException {
        Seq<Uri> bootStrapServers = asScalaBuffer(singletonList(Uri$.MODULE$.apply(new URI("localhost:9092")))).toSeq();

        return KafkaConfig.apply(new BootstrapConfig(bootStrapServers))
                .add(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName())
                .add(VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    }

    private static void produce(String topic, KafkaConfig config) {
        System.out.println(config.toProperties());

        KafkaProducer<String, String> producer = new KafkaProducer<>(config.toProperties());

        for (int i = 0; i < 10; i++) {
            String key = "id_" + i;
            String value = "hello world " + i;

            final ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

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
