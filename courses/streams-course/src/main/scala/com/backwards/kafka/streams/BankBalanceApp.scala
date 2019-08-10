package com.backwards.kafka.streams

import java.time.LocalDateTime
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
import io.circe.generic.auto._
import com.backwards.kafka.serde.circe.Serializer._
import com.backwards.kafka.serde.monix.Serializer._

/**
  * Create a Kafka producer that outputs ~100 messages per second to a topic.
  * Each message contains a random amount iterating over 6 customers.
  * JSON message example:
  * <pre>
  *   { "name": "John", "amount": 123, "time": "2019-07-19-19T-05:24:52" }
  * </pre>
  *
  * Create a Kafka Streams application that consumes these transactions and computes the total money in their balance,
  * and the latest time an update was received.
  */
object BankBalanceApp extends App with KafkaAdmin with LazyLogger {
  implicit val scheduler: Scheduler = monix.execution.Scheduler.global

  implicit val admin: AdminClient = newAdminClient()

  /*implicit val localDateTimeSerializer: Serializer[LocalDateTime] = new Serializer[LocalDateTime] {
    def serialize(topic: String, data: LocalDateTime): Array[Byte] = ???
  }*/

  val bootstrapServers = "127.0.0.1:9092"

  val transactionsTopic: NewTopic = createTopic("transactions", numberOfPartitions = 1, replicationFactor = 1)

  val producerCfg = KafkaProducerConfig.default.copy(
    bootstrapServers = List(bootstrapServers),
    maxBlockTime = 5 seconds
  )

  val producer = KafkaProducer[String, Transaction](producerCfg, scheduler)
  producer.send(transactionsTopic.name(), Transaction("Bob", 500)).runSyncUnsafe()
}

final case class Transaction(name: String, amount: Int/*, time: LocalDateTime*/)