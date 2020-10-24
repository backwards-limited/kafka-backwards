package com.backwards.kafka.producer

import java.util.concurrent.TimeUnit
import scala.annotation.tailrec
import cats.effect.{ExitCode, Fiber, IO, IOApp}
import cats.implicits._
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.apache.kafka.clients.producer.KafkaProducer

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
object PointOfSaleSimulatorApp extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    val v = for {
      implicit0(logger: Logger[IO]) <- Slf4jLogger.create[IO]
      _ = logger.info("Program bootstrapped...")
      producerProperties <- producerProperties
      x <- IO(new KafkaProducer[Int, String](producerProperties)).bracket(use)(release)
    } yield x

    v.redeem(t => ExitCode.Error, ls => ExitCode.Success)
  }

  def use(producer: KafkaProducer[Int, String])(implicit logger: Logger[IO]): IO[List[Any]] = {
    @tailrec
    def send: Any = {
      TimeUnit.SECONDS.sleep(5)
      println(Thread.currentThread().getName)
      send
    }

    IO(send).start.replicateA(5).flatMap(_.traverse(_.join))
  }

  def release(producer: KafkaProducer[Int, String])(implicit logger: Logger[IO]): IO[Unit] =
    logger.info("Closing Kafka Producer").map(_ => producer.close())
}