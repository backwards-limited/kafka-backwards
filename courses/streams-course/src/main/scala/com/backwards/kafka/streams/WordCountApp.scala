package com.backwards.kafka.streams

import scala.concurrent.duration._
import scala.language.postfixOps
import wvlet.log.LazyLogger
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, KTable}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import com.backwards.collection.MapOps._
import com.backwards.time.DurationOps._

/**
  * Write topology using high level DSL.
  * Remember data in Kafka Streams is "key -> value".
  * 1. Stream from Kafka                                  null -> "Kafka Kafka Streams"
  * 2. MapValues lowercase                                null -> "kafka kafka streams"
  * 3. FlatMapValues split by space                       null -> "kafka", null -> "kafka", null -> "streams"
  * 4. SelectKey to apply a key                           "kafka" -> "kafka", "kafka" -> "kafka", "streams" -> "streams"
  * 5. GroupByKey before aggregation                      ("kafka" -> "kafka", "kafka" -> "kafka"), ("streams" -> "streams")
  * 6. Count occurences in each group                     "kafka" -> 2, "streams" -> 1
  * 7. To in order to write results back to Kafka         Data point is written to Kafka
  *
  * To run this application:
  * 1. Boot Kafka
  *   - Either:
  *   - right-click and run "docker-compose.yml"
  *   - or on the command line
  *   - sbt "; project streams-course; dockerComposeUp"
  * 2. Create our two topics
  *   - kafka-topics --zookeeper localhost:2181 --create --replication-factor 1 --partitions 2 --topic word-count-input
  *   - kafka-topics --zookeeper localhost:2181 --create --replication-factor 1 --partitions 2 --topic word-count-output
  * 3. Run a "kafka console consumer" against the final topic
  *   - kafka-console-consumer --bootstrap-server localhost:9092 \
  *       --topic word-count-output \
  *       --from-beginning \
  *       --formatter kafka.tools.DefaultMessageFormatter \
  *       --property print.key=true \
  *       --property print.value=true \
  *       --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
  *       --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
  *   - and once the appliation and producer are running in 4. and 5. we'll have some results such as
  *   - kafka	2
  *   - streams	1
  *   - streaming	1
  *   - how	1
  *   - are	1
  *   - kafka	3
  *   - streams	2
  *   - hello	1
  *   - everybody	1
  *   - your	1
  * 4. Run this application
  *   - Either:
  *   - In Intellij, click the green run/arrow button in the margin of this app
  *   - or on the command line
  *   - sbt "; project streams-course; runMain com.backwards.kafka.streams.FavouriteColourApp"
  * 5. Using a "kafka console producer" publish messages onto the input topic
  *   - kafka-console-producer --broker-list localhost:9092 --topic word-count-input
  *   - >Kafka kafka streams streaming
  *   - >Hello everybody how are your Kafka Streams
  *   - >
  *
  * Running a Kafka Streams application may eventually create internal intermediary topics - there are two types:
  *   - Repartitioning topics: In case you start transforming the key of your stream a repartitioning will happen at some processor.
  *   - Changelog topics: In case you perform aggregations, Kafka Streams will save compacted data in these topics.
  *
  * Internal topics:
  *   - Are managed by Kafka Streams
  *   - Are used by Kafka Streams to save/restore state and repartition data
  *   - Are prefixed by "application.id" parameter
  *   - Should never be deleted, altered or published to
  *
  * Scaling - Our input topic has 2 partitions, therefore we can launch up to 2 instances of our application in paraller without any code changes.
  * This is because a Kafka Streams application relies on KafkaConsumer, and we can add consumers to a consumer group by just running the same code.
  * So scaling is easy, without the need of any application cluster.
  * If we build a fat jar, we can just run that jar multiple times.
  *
  * Assembly:
  *   - sbt "; project streams-course; assembly"
  *
  * Run twice:
  *   - java -jar courses/streams-course/target/scala-2.12/streams-course-0.1.0-SNAPSHOT.jar
  *   - java -jar courses/streams-course/target/scala-2.12/streams-course-0.1.0-SNAPSHOT.jar
  */
object WordCountApp extends App with LazyLogger {
  val props = Map(
    StreamsConfig.APPLICATION_ID_CONFIG -> "word-count",
    StreamsConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest"
  )

  val builder = new StreamsBuilder

  val wordCountStream: KStream[String, String] = builder.stream[String, String]("word-count-input")

  val wordCounts: KTable[String, Long] = wordCountStream
    .mapValues(_.toLowerCase)
    .flatMapValues(_.split(" "))
    .selectKey((_, word) => word)
    .groupByKey
    .count

  wordCounts.toStream.to("word-count-output")

  val streams: KafkaStreams = new KafkaStreams(builder.build(), props)
  streams.cleanUp()
  streams.start()

  logger info streams

  // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
  sys ShutdownHookThread streams.close(10 seconds)
}