package com.backwards.kafka.producer

import java.util.Properties
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxEitherId}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import pureconfig.ConfigSource
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.{IntegerSerializer, StringSerializer}
import com.backwards.collection._

/**
  * [[sbt master-realtime-stream-processing/run]]
  */
object SimpleProducer extends IOApp {
  lazy val defaultProducerProperties: Properties = {
    val props = new Properties
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "simple-producer")
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[IntegerSerializer].getName)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)

    props
  }

  def run(args: List[String]): IO[ExitCode] = {
    for {
      implicit0(logger: Logger[IO]) <- Slf4jLogger.create[IO]
      _ = logger.info("Program bootstrapped...")
      producerProperties <- producerProperties
      result <- program(producerProperties).redeemWith(programFailure(logger), programSuccess(logger))
    } yield result
  }

  def producerProperties(implicit Logger: Logger[IO]): IO[Properties] =
    IO(ConfigSource.default.at("simple-producer").load[Map[String, String]]).flatMap {
      case Right(producerConfig) =>
        IO(toProperties(producerConfig))

      case Left(configReaderFailures) =>
        Logger.error(s"Defaulting simple producer config since failed to load simple producer config: $configReaderFailures") *> IO(defaultProducerProperties)
    }

  def program(producerProperties: Properties)(implicit Logger: Logger[IO]): IO[Unit] = {
    def send(i: Int)(producer: KafkaProducer[Int, String]): IO[Unit] =
      if (i >= 100) {
        IO.unit
      } else {
        IO.async[Unit] { cb =>
          producer.send(
            new ProducerRecord("simple-producer-topic", i, s"Simple Message-$i"),
            (_: RecordMetadata, exception: Exception) => Option(exception).fold(cb(().asRight))(_.asLeft)
          )
        } *> send(i + 1)(producer)
      }

    IO(new KafkaProducer[Int, String](producerProperties)).bracket(send(0))(_.close().pure[IO])
  }

  def programSuccess(logger: Logger[IO]): Unit => IO[ExitCode] =
    _ => logger.info("Program success").map(_ => ExitCode.Success)

  def programFailure(logger: Logger[IO]): Throwable => IO[ExitCode] =
    t => logger.error(t)("Program failure").map(_ => ExitCode.Error)
}