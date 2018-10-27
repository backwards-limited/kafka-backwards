package com.backwards.twitter

import scala.annotation.tailrec
import scala.language.{higherKinds, postfixOps}
import cats.effect.IO
import org.apache.kafka.clients.consumer.ConsumerConfig.{AUTO_OFFSET_RESET_CONFIG, GROUP_ID_CONFIG}
import com.backwards.config.kafkaConfig
import com.backwards.kafka.Consumer
import com.backwards.kafka.config.KafkaConfig
import com.backwards.kafka.serde.Serde
import com.backwards.logging.Logging

object TwitterConsumer {
  def apply(topic: String) =
    new TwitterConsumer(topic, kafkaConfig + (GROUP_ID_CONFIG, "twitter-group") + (AUTO_OFFSET_RESET_CONFIG -> "earliest"))
}

class TwitterConsumer private(topic: String, config: KafkaConfig) extends Serde.Implicits with Logging {
  val consumer: Consumer[IO, String, String] = Consumer[IO, String, String](topic, config) // TODO - config: max.poll.records

  def consume: IO[Seq[(String, String)]] = consumer.poll()

  /**
    * Here is a good example of a naive approach (appropriate for this beginners course) - in a more advanced module we shall discover the likes of FS2 with Kafka.
    * @param callback Seq[(String, String)] => Unit
    * @return IO[Unit]
    */
  def doConsume(callback: Seq[(String, String)] => Unit): IO[Unit] = {
    @tailrec
    def go(): Unit = {
      callback(consume.unsafeRunSync)
      go()
    }

    IO(go())
  }
}