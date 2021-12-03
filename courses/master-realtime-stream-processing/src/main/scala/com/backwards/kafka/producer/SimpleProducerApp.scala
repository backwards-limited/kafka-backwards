package com.backwards.kafka.producer

import java.util.Properties
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxEitherId}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}

/**
  * [[sbt master-realtime-stream-processing/run]]
  */
object SimpleProducerApp extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    for {
      implicit0(logger: Logger[IO]) <- Slf4jLogger.create[IO]
      _ = logger.info("Program bootstrapped...")
      producerProperties <- producerProperties
      result <- program(producerProperties).redeemWith(programFailure(logger), programSuccess(logger))
    } yield result
  }

  def program(producerProperties: Properties)(implicit Logger: Logger[IO]): IO[Unit] = {
    def send(i: Int)(producer: KafkaProducer[Int, String]): IO[Unit] =
      if (i >= 100) {
        IO.unit
      } else {
        IO.async_[Unit](cb =>
          producer.send(
            new ProducerRecord("simple-producer-topic", i, s"Simple Message-$i"),
            (_: RecordMetadata, exception: Exception) => Option(exception).fold(cb(().asRight))(_.asLeft)
          )
        ) >> send(i + 1)(producer)
      }

    IO(new KafkaProducer[Int, String](producerProperties)).bracket(send(0))(_.close().pure[IO])
  }

  def programSuccess(logger: Logger[IO]): Unit => IO[ExitCode] =
    _ => logger.info("Program success").as(ExitCode.Success)

  def programFailure(logger: Logger[IO]): Throwable => IO[ExitCode] =
    t => logger.error(t)("Program failure").as(ExitCode.Error)
}