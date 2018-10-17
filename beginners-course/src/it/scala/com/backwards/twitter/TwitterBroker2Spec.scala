package com.backwards.twitter

import cats.effect.IO
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.common.serialization.{Serializer, StringSerializer}
import org.scalatest.{MustMatchers, OneInstancePerTest, WordSpec}
import com.backwards.container._
import com.backwards.kafka.{Configuration, KafkaContainer, Producer, ZookeeperContainer}
import com.backwards.logging.Logging

/**
  * Example of showing "container for each".
  * Note the gimmick "ignore" parameter, to avoid running this spec - it is only here for example purposes.
  */
class TwitterBroker2Spec(ignore: String) extends WordSpec with MustMatchers with ContainerFixture with ForEachContainerLifecycle with OneInstancePerTest with Logging {
  lazy val zookeeperContainer = ZookeeperContainer()
  lazy val kafkaContainer = KafkaContainer(zookeeperContainer)

  lazy val container: Container = Container(zookeeperContainer, kafkaContainer)

  "Twitter brokerage" should {
    "send a tweet to Kafka" in {
      val configuration = Configuration("twitter-topic") + (BOOTSTRAP_SERVERS_CONFIG -> kafkaContainer.uri.toString)

      implicit val stringSerializer: Serializer[String] = new StringSerializer

      val twitterProducer = Producer[IO, String, String](configuration)

      twitterProducer.send("key", "tweet").unsafeRunSync() must matchPattern { case Right(_) => }
    }

    "send another tweet to Kafka" in {
      val configuration = Configuration("twitter-topic") + (BOOTSTRAP_SERVERS_CONFIG -> kafkaContainer.uri.toString)

      implicit val stringSerializer: Serializer[String] = new StringSerializer

      val twitterProducer = Producer[IO, String, String](configuration)

      twitterProducer.send("key", "tweet").unsafeRunSync() must matchPattern { case Right(_) => }
    }
  }
}