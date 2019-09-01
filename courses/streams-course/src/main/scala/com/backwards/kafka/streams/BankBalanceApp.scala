package com.backwards.kafka.streams

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps
import cats.effect.{ContextShift, IO, Timer}
import fs2.Stream
import io.circe.generic.auto._
import monix.execution.Scheduler
import monix.kafka._
import wvlet.log.LazyLogger
import org.apache.kafka.clients.admin.{AdminClient, AdminClientConfig, NewTopic}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KTable
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import com.backwards.collection.MapOps._
import com.backwards.kafka.admin.KafkaAdmin
import com.backwards.kafka.serde.circe.Serializer._
import com.backwards.kafka.serde.circe.Deserializer._
import com.backwards.kafka.serde.monix.Serializer._
import com.backwards.kafka.serde.monix.Deserializer._
import com.backwards.kafka.serde.Serde._
import com.backwards.time.DurationOps._
import cats.implicits._
import shapeless._
import com.backwards.kafka.serde.Serde

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
object BankBalanceApp extends App with KafkaAdmin with LazyLogger with com.backwards.kafka.serde.Serde {
  val bootstrapServers = List("127.0.0.1:9092")

  implicit val admin: AdminClient = newAdminClient(
    Map(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG -> bootstrapServers.mkString(","))
  )

  val transactionsTopic: NewTopic = createTopic("transactions", numberOfPartitions = 1, replicationFactor = 1)
  val transactionsAggregateTopic: NewTopic = createTopic(s"${transactionsTopic.name()}-aggregate", numberOfPartitions = 1, replicationFactor = 1)

  doTransactions(bootstrapServers, transactionsTopic)
  println(s"===> doing transactions")
  consumeTransactions(bootstrapServers, transactionsTopic)
  println(s"===> consuming transactions")

  // TODO - Return either Stream or IO and then for comprehension that
  def doTransactions(bootstrapServers: List[String], transactionsTopic: NewTopic): Unit = {
    val ec: ExecutionContext = ExecutionContext.global
    implicit val cs: ContextShift[IO] = IO.contextShift(ec)
    implicit val timer: Timer[IO] = IO.timer(ec)
    implicit val scheduler: Scheduler = monix.execution.Scheduler.global

    val producerCfg = KafkaProducerConfig.default.copy(
      bootstrapServers = bootstrapServers,
      maxBlockTime = 5 seconds
    )

    val producer = KafkaProducer[String, Transaction](producerCfg, scheduler)

    val stream =
      Stream("Bob", "Sue", "Bill", "Agnes", "Mary", "Sid").repeat
        .zipLeft(Stream.awakeEvery[IO](5.seconds))
        .evalTap(user => IO.fromFuture(IO(producer.send(transactionsTopic.name(), Transaction(user, 500, LocalDateTime.now())).map(_ => ()).runToFuture)))

    stream
      .interruptAfter(30 seconds)
      .compile.drain.unsafeRunAsyncAndForget()
  }

  def consumeTransactions(bootstrapServers: List[String], transactionsTopic: NewTopic): Unit = {
    val props = Map(
      StreamsConfig.APPLICATION_ID_CONFIG -> "transactions-aggregator",
      StreamsConfig.BOOTSTRAP_SERVERS_CONFIG -> bootstrapServers.mkString(","),
      StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG -> "0", // Disable cache to demonstrate all "steps" involved in the transformation - not recommended in prod
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest"
    )

    val builder = new StreamsBuilder

    // TODO - Wrong - where is implicit serde?
    // TODO - Also, when run we get: [WARN ] o.a.k.s.k.i.KTableSource - Skipping record due to null key. topic=[transactions] partition=[0] offset=[0]
    implicit val x = new org.apache.kafka.common.serialization.Serde[Transaction] {
      def serializer(): org.apache.kafka.common.serialization.Serializer[Transaction] = circeSerializer[Transaction]

      def deserializer(): org.apache.kafka.common.serialization.Deserializer[Transaction] = circeDeserializer[Transaction]
    }

    val transactionsTable = builder.table[String, Transaction](transactionsTopic.name())

    val transactionsAggregateTable: KTable[String, Int] = transactionsTable
      .groupBy((_, transaction) => (transaction.name, transaction.amount))
      .reduce((amount1, amount2) => amount1 + amount2, (i1, i2) => i1 - i2)

    transactionsAggregateTable.toStream.to(transactionsAggregateTopic.name())

    val streams = new KafkaStreams(builder.build(), props)
    streams.cleanUp() // Just for dev (not prod)
    streams.start()
    println(s"===> started streams")

    // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
    sys ShutdownHookThread streams.close(10 seconds)
  }
}

final case class Transaction(name: String, amount: Int, time: LocalDateTime)