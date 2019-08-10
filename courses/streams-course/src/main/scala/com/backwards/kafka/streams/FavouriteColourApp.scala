package com.backwards.kafka.streams

import scala.concurrent.duration._
import scala.language.postfixOps
import monix.eval.Task
import monix.execution.Scheduler
import monix.kafka._
import wvlet.log.LazyLogger
import org.apache.kafka.clients.admin.{AdminClient, NewTopic}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KTable
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import com.backwards.collection.MapOps._
import com.backwards.kafka.admin.KafkaAdmin
import com.backwards.time.DurationOps._

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
  *
  * We can check the final aggregation topic with:
  * <pre>
  *   kafka-console-consumer --bootstrap-server localhost:9092 \
  *     --topic favourite-colours-aggregate \
  *     --from-beginning \
  *     --formatter kafka.tools.DefaultMessageFormatter \
  *     --property print.key=true \
  *     --property print.value=true \
  *     --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
  *     --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
  * </pre>
  *
  * Or, since the Kafka image used by this module's docker compose has a nice UI, just navigate to:
  * http://127.0.0.1:3030
  */
object FavouriteColourApp extends App with KafkaAdmin with LazyLogger {
  type Key = String
  type Value = String

  implicit val scheduler: Scheduler = monix.execution.Scheduler.global

  implicit val admin: AdminClient = newAdminClient()

  val bootstrapServers = "127.0.0.1:9092"

  val inputTopic: NewTopic = createTopic("favourite-colours", numberOfPartitions = 1, replicationFactor = 1)
  val aggregateTopic: NewTopic = createTopic(s"${inputTopic.name()}-aggregate", numberOfPartitions = 1, replicationFactor = 1)

  val producerCfg = KafkaProducerConfig.default.copy(
    bootstrapServers = List(bootstrapServers),
    maxBlockTime = 5 seconds
  )

  val producer = KafkaProducer[Key, Value](producerCfg, scheduler)

  val produce: ((Key, Value)) => Task[Option[RecordMetadata]] = {
    case (key, value) => producer.send(inputTopic.name(), key, value)
  }

  Task.traverse(
    Seq("stephane" -> "blue", "john" -> "green", "stephane" -> "red", "alice" -> "red")
  )(produce).runSyncUnsafe()

  val props = Map(
    StreamsConfig.APPLICATION_ID_CONFIG -> "favourite-colours-aggregator",
    StreamsConfig.BOOTSTRAP_SERVERS_CONFIG -> bootstrapServers,
    StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG -> "0", // Disable cache to demonstrate all "steps" involved in the transformation - not recommended in prod
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest"
  )

  val builder = new StreamsBuilder

  val favouriteColoursStream: KTable[String, String] = builder.table[String, String](inputTopic.name())

  val favouriteColours: KTable[String, Long] = favouriteColoursStream
    .mapValues(_.toLowerCase)
    .filter((_, value) => Seq("red", "green", "blue") contains value)
    .groupBy((_, value) => (value, value))
    .count

  favouriteColours.toStream.to(aggregateTopic.name())

  val streams: KafkaStreams = new KafkaStreams(builder.build(), props)
  streams.cleanUp() // Just for dev (not prod)
  streams.start()

  // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
  sys ShutdownHookThread streams.close(10 seconds)
}