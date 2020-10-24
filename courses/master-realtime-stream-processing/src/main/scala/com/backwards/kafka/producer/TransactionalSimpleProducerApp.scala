package com.backwards.kafka.producer

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord, RecordMetadata}

/**
  * [[sbt master-realtime-stream-processing/run]]
  *
  * {{{
  *   kafka-console-consumer --bootstrap-server localhost:9092 --from-beginning --whitelist "hello-producer-1 | hello-producer-2"
  * }}}
  */
object TransactionalSimpleProducerApp extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    for {
      implicit0(logger: Logger[IO]) <- Slf4jLogger.create[IO]
      _ = logger.info("Program bootstrapped...")
      producerProperties <- producerProperties.map { props =>
        // Required by a "transactional" producer
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "transactional-id")
        props
      }
      result <- IO(new KafkaProducer[Int, String](producerProperties)).bracket(produce)(release).redeemWith(programFailure(logger), programSuccess(logger))
    } yield result
  }

  def produce(producer: KafkaProducer[Int, String])(implicit logger: Logger[IO]): IO[Unit] = {
    def produceInTxn(id: Int): IO[Unit] =
      IO(producer.beginTransaction()) *> produce(id, producer)

    IO(producer.initTransactions()) *> produceInTxn(1) *> IO(producer.commitTransaction()) *> produceInTxn(2) *> IO(producer.abortTransaction())
  }

  def produce(id: Int, producer: KafkaProducer[Int, String])(implicit logger: Logger[IO]): IO[Unit] = {
    def send(i: Int): IO[Unit] =
      if (i >= 2) {
        IO.unit
      } else {
        def produce(topic: String): IO[Unit] = IO.async[Unit] { cb =>
          producer.send(
            new ProducerRecord(topic, i, s"Message-$id-$i: $topic"),
            (_: RecordMetadata, exception: Exception) => Option(exception).fold(cb(().asRight))(_.asLeft)
          )
        }

        List(produce("hello-producer-1"), produce("hello-producer-2")).parSequence *> send(i + 1)
      }

    send(0)
  }

  def release(producer: KafkaProducer[Int, String]): IO[Unit] =
    IO(producer.close())

  def programSuccess(logger: Logger[IO]): Unit => IO[ExitCode] =
    _ => logger.info("Program success").map(_ => ExitCode.Success)

  def programFailure(logger: Logger[IO]): Throwable => IO[ExitCode] =
    t => logger.error(t)("Program failure").map(_ => ExitCode.Error)
}