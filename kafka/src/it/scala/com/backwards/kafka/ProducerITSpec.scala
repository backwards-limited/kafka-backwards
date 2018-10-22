package com.backwards.kafka

import cats.effect.IO
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.common.serialization.{Serializer, StringSerializer}
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.container.{Container, ContainerFixture}

trait ProducerITSpec extends WordSpec with MustMatchers {
  this: ContainerFixture =>

  lazy val zookeeperContainer = ZookeeperContainer()
  lazy val kafkaContainer = KafkaContainer(zookeeperContainer)

  lazy val container: Container = Container(zookeeperContainer, kafkaContainer)

  "Kafka producer" should {
    "send a message to Kafka" in {
      val configuration = Configuration("test-topic") + (BOOTSTRAP_SERVERS_CONFIG -> kafkaContainer.uri.toString)

      implicit val stringSerializer: Serializer[String] = new StringSerializer

      val producer = Producer[IO, String, String](configuration)

      producer.send("key", "value").unsafeRunSync() must matchPattern { case Right(_) => }
    }

    "send another message to Kafka" in {
      val configuration = Configuration("test-topic") + (BOOTSTRAP_SERVERS_CONFIG -> kafkaContainer.uri.toString)

      implicit val stringSerializer: Serializer[String] = new StringSerializer

      val producer = Producer[IO, String, String](configuration)

      producer.send("key", "value").unsafeRunSync() must matchPattern { case Right(_) => }
    }
  }
}