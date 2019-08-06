package com.backwards.kafka.streams

import scala.concurrent.duration._
import scala.language.postfixOps
import monix.eval.Task
import wvlet.log.LazyLogger
import org.apache.kafka.clients.admin.{AdminClient, NewTopic}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.{KStream, KTable}
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import com.backwards.collection.MapOps._
import com.backwards.kafka.admin.KafkaAdmin
import com.backwards.time.DurationOps._
import monix.kafka._
import monix.execution.Scheduler
import org.apache.kafka.clients.producer.RecordMetadata

/**
  * Take a comma delimited topic of userId -> colour
  *   - Filter out bad data
  *   - Keep only colours red, green and blue
  * Get the running count of the favourite colours overall and output to a topic
  * Note that a user's favourite colour can change.
  * <img src="doc-files/favourite-colour.png"/>
  *
  * So, we do the following:
  *   - Write topology
  *   - Find right transformations to apply
  *   - Create input and output topics (and if necessary any intermediary topics)
  *   - And feed the following sample data from a producer:
  *     - stephane,blue
  *     - john,green
  *     - stephane,red
  *     - alice,red
  */
object FavouriteColourApp extends App with KafkaAdmin with LazyLogger {
  type Key = String
  type Value = String

  implicit val admin: AdminClient = newAdminClient()

  val topic: NewTopic = createTopic("favourite-colours", numberOfPartitions = 1, replicationFactor = 1)


  implicit val scheduler: Scheduler = monix.execution.Scheduler.global

  val producerCfg = KafkaProducerConfig.default.copy(
    bootstrapServers = List("127.0.0.1:9092")
  )

  val producer = KafkaProducer[Key, Value](producerCfg, scheduler)

  val produce: ((Key, Value)) => Task[Option[RecordMetadata]] = {
    case (key, value) => producer.send(topic.name(), key, value)
  }

  Task.traverse(
    Seq("stephane" -> "blue", "john" -> "green", "stephane" -> "red", "alice" -> "red")
  )(produce).runSyncUnsafe()



  /*val props = Map(
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
  sys ShutdownHookThread streams.close(10 seconds)*/
}