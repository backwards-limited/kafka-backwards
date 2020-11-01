package com.backwards.kafka.producer

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import cats.effect.{ExitCode, IO}
import cats.implicits._
import io.chrisdavenport.cats.effect.time.JavaTime
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.circe.generic.AutoDerivation
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, RecordMetadata}
import com.monovore.decline._
import com.monovore.decline.effect._

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
object PointOfSaleSimulatorApp extends CommandIOApp(name = "PoS", header = "Run Point of Sales issuing invoices") with ValueClassCodec with AutoDerivation {
  lazy val invoiceCount: Opts[Int] =
    Opts.option[Int]("invoice-count", help = "Number of invoices").withDefault(3)

  override def main: Opts[IO[ExitCode]] = {
    invoiceCount map { invoiceCount =>
      val program = for {
        implicit0(logger: Logger[IO]) <- Slf4jLogger.create[IO]
        _ = logger.info("Program bootstrapped...")
        producerProperties <- producerProperties
        _ <- IO(Kafka.circe.producer[String, Invoice](producerProperties)).bracket(use(invoiceCount))(release)
      } yield ()

      program.redeemWith(t => IO(t.printStackTrace()) *> IO(ExitCode.Error), _ => IO(ExitCode.Success))
    }
  }

  def use(invoiceCount: Int = 3)(producer: KafkaProducer[String, Invoice])(implicit logger: Logger[IO]): IO[Unit] = {
    def send(invoiceCount: Int): IO[Unit] =
      IO.asyncF[Int] { cb =>
        for {
          instant <- JavaTime[IO].getInstant
          invoice = Invoice(InvoiceId("id"), InvoiceNumber("invoice-number"), instant, InvoiceStoreId("store-id"))
          _ <- logger.info(s"Sending $invoice")
        } yield
          producer.send(
            new ProducerRecord("pos-topic", invoice.id.value, invoice),
            (_: RecordMetadata, exception: Exception) => Option(exception).fold(cb((invoiceCount - 1).asRight))(_.asLeft)
          )
      } flatMap { invoiceCount =>
        IO.whenA(invoiceCount > 0)(IO.sleep(5 seconds) *> send(invoiceCount))
      }

    send(invoiceCount).start.replicateA(5).flatMap(_.traverse(_.join)) *> IO.unit
  }

  def release(producer: KafkaProducer[String, Invoice])(implicit logger: Logger[IO]): IO[Unit] =
    logger.info("Closing Kafka Producer").map(_ => producer.close())
}