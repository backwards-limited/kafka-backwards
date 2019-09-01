package com.backwards.kafka.streams

import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps
import cats.effect.{ContextShift, IO, Timer}
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
import fs2.Stream

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
  implicit val admin: AdminClient = newAdminClient()

  val bootstrapServers = List("127.0.0.1:9092")

  val transactionsTopic: NewTopic = createTopic("transactions", numberOfPartitions = 1, replicationFactor = 1)

  doTransactions(bootstrapServers)
  //consumeTransactions(bootstrapServers)

  def doTransactions(bootstrapServers: List[String]): Unit = {
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
}

final case class Transaction(name: String, amount: Int, time: LocalDateTime)