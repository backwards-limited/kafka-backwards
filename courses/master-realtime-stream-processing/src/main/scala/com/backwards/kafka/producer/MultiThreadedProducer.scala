package com.backwards.kafka.producer

import java.util.Properties
import scala.concurrent.Promise
import scala.io.Source
import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxEitherId}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import pureconfig.ConfigSource
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.{IntegerSerializer, StringSerializer}
import com.backwards.collection._

/**
  * [[sbt master-realtime-stream-processing/run]]
  *
  * A multi-threaded Kafka Producer that sends data from a list of files to a Kafka topic such that independent threads stream each file.
  * E.g. 3 files therefore 3 threads.
  */
object MultiThreadedProducer extends IOApp {
  lazy val defaultProducerProperties: Properties = {
    val props = new Properties
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "multi-threaded-producer")
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
      //sources = List(IO(Source.fromResource("NSE05NOV2018BHAV.csv")), IO(Source.fromResource("NSE06NOV2018BHAV.csv")))
      sources = List(IO(Source.fromResource("temp.csv")), IO(Source.fromResource("temp2.csv")))
      result <- program(producerProperties, sources).redeemWith(programFailure(logger), programSuccess(logger))
    } yield result
  }

  def producerProperties(implicit Logger: Logger[IO]): IO[Properties] = IO(ConfigSource.default.at("multi-threaded-producer").load[Map[String, String]]).flatMap {
    case Right(producerConfig) =>
      IO(toProperties(producerConfig))

    case Left(configReaderFailures) =>
      Logger.error(s"Defaulting simple producer config since failed to load simple producer config: $configReaderFailures") *> IO(defaultProducerProperties)
  }

  def program(producerProperties: Properties, sources: List[IO[Source]])(implicit Logger: Logger[IO]): IO[Unit] = {
    import cats.syntax.parallel._

    def send(source: Source)(producer: KafkaProducer[Int, String]): IO[Unit] = {
      val promise = Promise[Unit]()

      def send(lines: Iterator[String]): IO[Unit] = {
        if (lines.hasNext) {
          val line = lines.next()
          // Logger.info() TODO
          println(s"===> $line")

          producer.send(
            new ProducerRecord("multi-threaded-producer-topic" /*source.size*/, line), // TODO - key
            (_: RecordMetadata, exception: Exception) => if (exception == null) send(lines) else promise.failure(exception)
          )
        } else {
          promise.success(())
        }

        IO.fromFuture(IO(promise.future))
      }

      send(source.getLines)
    }

    sources.map { source =>
      Resource.fromAutoCloseable(source).use { source =>
        IO(new KafkaProducer[Int, String](producerProperties)).bracket(send(source))(_.close().pure[IO])
      }
    }.parSequence *> IO.unit
  }

  def programSuccess(logger: Logger[IO]): Unit => IO[ExitCode] =
    _ => logger.info("Program success").map(_ => ExitCode.Success)

  def programFailure(logger: Logger[IO]): Throwable => IO[ExitCode] =
    t => logger.error(t)("Program failure").map(_ => ExitCode.Error)
}