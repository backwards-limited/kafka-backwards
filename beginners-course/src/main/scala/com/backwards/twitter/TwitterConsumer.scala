package com.backwards.twitter

import scala.language.{higherKinds, postfixOps}
import cats.effect.IO
import org.apache.kafka.clients.consumer.ConsumerConfig.{AUTO_OFFSET_RESET_CONFIG, GROUP_ID_CONFIG}
import com.backwards.kafka.serde.Serde
import com.backwards.kafka.{Configuration, Consumer}
import com.backwards.logging.Logging

object TwitterConsumer {
  def apply(configuration: Configuration) =
    new TwitterConsumer(configuration + (GROUP_ID_CONFIG, "twitter-group") + (AUTO_OFFSET_RESET_CONFIG -> "earliest"))
}

class TwitterConsumer private(configuration: Configuration) extends Serde.Implicits with Logging {
  val consumer: Consumer[IO, String, String] = Consumer[IO, String, String](configuration)

  def consume(): IO[Seq[(String, String)]] = consumer.poll()

  def doConsume(callback: Throwable Either Seq[(String, String)] => IO[Unit]): Unit =
    consume().runAsync(callback).unsafeRunSync()
}