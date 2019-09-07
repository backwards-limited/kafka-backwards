package com.backwards.kafka.streams

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Random
import cats.effect.{ContextShift, IO, Timer}
import cats.implicits._
import fs2.Stream
import io.circe.generic.auto._
import monix.execution.Scheduler
import monix.kafka._
import monix.kafka.config.Acks
import wvlet.log.LazyLogger
import org.apache.kafka.clients.admin.{AdminClient, AdminClientConfig, NewTopic}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.Serdes._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KTable
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import com.backwards.collection.MapOps._
import com.backwards.kafka.admin.KafkaAdmin
import com.backwards.kafka.serde.Serde._
import com.backwards.kafka.serde.circe.Deserializer._
import com.backwards.kafka.serde.circe.Serializer._
import com.backwards.kafka.serde.monix.Serializer._
import com.backwards.time.DurationOps._

/**
  * Create a Kafka producer that outputs ~10 messages per second to a topic.
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
  val bootstrapServers = List("127.0.0.1:9092")

  implicit val admin: AdminClient = newAdminClient(
    Map(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG -> bootstrapServers.mkString(","))
  )

  val transactionsTopic: NewTopic = createTopic("transactions", numberOfPartitions = 1, replicationFactor = 1)
  val transactionsAggregateTopic: NewTopic = createTopic(s"${transactionsTopic.name()}-aggregate", numberOfPartitions = 1, replicationFactor = 1)

  doTransactions(bootstrapServers, transactionsTopic)
  consumeTransactions(bootstrapServers, transactionsTopic, transactionsAggregateTopic)

  // TODO - Return either Stream or IO and then for comprehension
  /**
    * kafkacat -b localhost:9092 -t transactions -C -o beginning
    * @param bootstrapServers List[String]
    * @param transactionsTopic NewTopic
    */
  def doTransactions(bootstrapServers: List[String], transactionsTopic: NewTopic): Unit = {
    val ec: ExecutionContext = ExecutionContext.global
    implicit val cs: ContextShift[IO] = IO.contextShift(ec)
    implicit val timer: Timer[IO] = IO.timer(ec)
    implicit val scheduler: Scheduler = monix.execution.Scheduler.global

    val producerCfg = KafkaProducerConfig.default.copy(
      bootstrapServers = bootstrapServers,
      maxBlockTime = 5 seconds,
      acks = Acks.All, // Must set acks to all in order to use the idempotent producer - otherwise cannot guarantee idempotence
      retries = 3, // Retries must be greater than 0 whn using idempotent producer
      properties = Map(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG -> true.toString) // Ensure we don't push duplicates
    )

    val producer = KafkaProducer[String, Transaction](producerCfg, scheduler)

    val randomAmount = () => Random.nextInt(100)

    val stream =
      Stream("Bob", "Sue", "Bill", "Agnes", "Mary", "Sid").repeat.zip(Stream(randomAmount).repeat)
        .zipLeft(Stream.awakeEvery[IO](1 second))
        .evalTap { case (user, randomAmount) =>
          IO.fromFuture(IO(producer.send(transactionsTopic.name(), user, Transaction(user, randomAmount(), LocalDateTime.now)).map(_ => ()).runToFuture))
        }

    stream
      .interruptAfter(30 seconds)
      .compile.drain.unsafeRunAsyncAndForget()
  }

  /**
    * kafkacat -b localhost:9092 -t transactions-aggregate -C -o beginning
    * @param bootstrapServers List[String]
    * @param transactionsTopic NewTopic
    * @param transactionsAggregateTopic NewTopic
    */
  def consumeTransactions(bootstrapServers: List[String], transactionsTopic: NewTopic, transactionsAggregateTopic: NewTopic): Unit = {
    val props = Map(
      StreamsConfig.APPLICATION_ID_CONFIG -> "transactions-aggregator",
      StreamsConfig.BOOTSTRAP_SERVERS_CONFIG -> bootstrapServers.mkString(","),
      StreamsConfig.PROCESSING_GUARANTEE_CONFIG -> StreamsConfig.EXACTLY_ONCE, // Exactly once processing
      StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG -> "0", // Disable cache to demonstrate all "steps" involved in the transformation - not recommended in prod
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest"
    )

    val builder = new StreamsBuilder

    val transactionsStream = builder.stream[String, Transaction](transactionsTopic.name())

    val transactionsAggregateTable: KTable[String, Transaction] = transactionsStream
      .groupByKey
      .aggregate[Transaction](genesisTransaction) { (user, transaction, accTransaction) =>
        Transaction(user, accTransaction.amount + transaction.amount, latestDateTime(transaction.time, accTransaction.time))
      }

    transactionsAggregateTable.toStream.to(transactionsAggregateTopic.name())

    val streams = new KafkaStreams(builder.build(), props)
    streams.cleanUp() // Just for dev (not prod)
    streams.start()

    // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
    sys ShutdownHookThread streams.close(10 seconds)
  }

  def genesisTransaction = Transaction(name = "", amount = 0, time = LocalDateTime.now)

  def latestDateTime(dts: LocalDateTime*): LocalDateTime =
    (LocalDateTime.now /: dts) {
      case (dt, latest) if dt isAfter latest => dt
      case (_, latest) => latest
    }
}