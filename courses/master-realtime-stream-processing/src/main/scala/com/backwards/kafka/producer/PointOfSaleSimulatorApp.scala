package com.backwards.kafka.producer

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import io.chrisdavenport.cats.effect.time.JavaTime
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.circe.generic.AutoDerivation
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}
import com.backwards.kafka.producer.Invoice._

/**
  * Multithreaded event producer
  *
  *   - thread 1 ------> JSON invoice ---------->
  *   - thread 2 ------> JSON invoice ---------->  Kafka
  *   - thread 3 ------> JSON invoice ---------->
  *
  * Bootstrap app with command line arguments:
  *
  *   - topic name
  *   - number of producer threads
  *   - produce speed
  */
object PointOfSaleSimulatorApp extends IOApp with ValueClassCodec with AutoDerivation {
  def run(args: List[String]): IO[ExitCode] = {
    val v = for {
      implicit0(logger: Logger[IO]) <- Slf4jLogger.create[IO]
      _ = logger.info("Program bootstrapped...")
      producerProperties <- producerProperties
      x <- IO(Kafka.circe.producer[String, Invoice](producerProperties)).bracket(use)(release)
    } yield x

    v.redeem(t => ExitCode.Error, ls => ExitCode.Success)
  }

  def use(producer: KafkaProducer[String, Invoice])(implicit logger: Logger[IO]): IO[List[Any]] = {
    def send: IO[Unit] = {
      IO.asyncF[Unit] { cb =>
        for {
          instant <- JavaTime[IO].getInstant
          invoice = Invoice(InvoiceId("id"), InvoiceNumber("invoice-number"), instant, InvoiceStoreId("store-id"))
          _ <- logger.info(s"Sending $invoice")
        } yield
          producer.send(
            new ProducerRecord("pos-topic", invoice.id.value, invoice),
            (_: RecordMetadata, exception: Exception) => Option(exception).fold(cb(().asRight))(_.asLeft)
          )
      }.flatMap(_ => IO.sleep(5 seconds)).flatMap(_ => send)
    }

    send.start.replicateA(5).flatMap(_.traverse(_.join))
  }

  def release(producer: KafkaProducer[String, Invoice])(implicit logger: Logger[IO]): IO[Unit] =
    logger.info("Closing Kafka Producer").map(_ => producer.close())
}