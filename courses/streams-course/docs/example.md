# Example

This example (WordCount streams) comes with Kafka. So first download [Kafka](https://kafka.apache.org/downloads).

Navigate to the folder you downloaded the binaries and apply the following steps.

Start Zookeeper:

```bash
/Applications/kafka_2.12-2.3.0
➜ bin/zookeeper-server-start.sh config/zookeeper.properties
```

Start Kafka:

```bash
/Applications/kafka_2.12-2.3.0
➜ bin/kafka-server-start.sh config/server.properties
```

Create an input topic:

```bash
/Applications/kafka_2.12-2.3.0
➜ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic streams-plaintext-input
```

Create an output topic:

```bash
/Applications/kafka_2.12-2.3.0
➜ bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic streams-wordcount-output
```

Assert the created topics:

```bash
/Applications/kafka_2.12-2.3.0
➜ bin/kafka-topics.sh --zookeeper localhost:2181 --list
streams-plaintext-input
streams-wordcount-output
```

Start a Kafka producer; enter text; and then Ctrl-C:

```bash
/Applications/kafka_2.12-2.3.0
➜ bin/kafka-console-producer.sh --broker-list localhost:9092 --topic streams-plaintext-input
>kafka streams udemy
>kafka data processing
>kafka streams course
>^C%
```

Just verify that all the submitted words are on the topic (and Ctrl-C when done):

```bash
/Applications/kafka_2.12-2.3.0
➜ bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic streams-plaintext-input --from-beginning
kafka streams udemy
kafka data processing
kafka streams course
^CProcessed a total of 3 messages
```

Before booting the WordCount streaming application, let's boot a consumer for the **streams-wordcount-output** topic:

```bash
/Applications/kafka_2.12-2.3.0
➜ bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
    --topic streams-wordcount-output \
    --from-beginning \
    --formatter kafka.tools.DefaultMessageFormatter \
    --property print.key=true \
    --property print.value=true \
    --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
    --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
```

where the following output will be shown upon booting the WordCount streams application:

```bash
kafka	1
streams	1
udemy	1
kafka	2
data	1
processing	1
kafka	3
streams	2
course	1
```

Finally start the WordCount streams application:

```bash
/Applications/kafka_2.12-2.3.0
➜ bin/kafka-run-class.sh org.apache.kafka.streams.examples.wordcount.WordCountDemo
```

Note that Kafka Streams will create "internal" topics:

```bash
/Applications/kafka_2.12-2.3.0
➜ bin/kafka-topics.sh --zookeeper localhost:2181 --list
__consumer_offsets
streams-plaintext-input
streams-wordcount-KSTREAM-AGGREGATE-STATE-STORE-0000000003-changelog
streams-wordcount-KSTREAM-AGGREGATE-STATE-STORE-0000000003-repartition
streams-wordcount-output
```

