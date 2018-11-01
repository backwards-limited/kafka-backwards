package com.backwards.kafka

import cats.effect.IO
import io.lemonlabs.uri.Uri
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.config.BootstrapConfig
import com.backwards.container.{Container, ContainerFixture}
import com.backwards.kafka.config.KafkaConfig
import com.backwards.kafka.serde.Serde
import com.backwards.transform.Transform

trait ProducerITSpec extends WordSpec with MustMatchers with Serde.Implicits with Transform.Implicits {
  this: ContainerFixture =>

  lazy val zookeeperContainer = ZookeeperContainer()
  lazy val kafkaContainer = KafkaContainer(zookeeperContainer)

  lazy val container: Container = Container(zookeeperContainer, kafkaContainer)

  "Kafka producer" should {
    "send a message to Kafka" in {
      val config = KafkaConfig(BootstrapConfig(Seq(Uri.parse(kafkaContainer.uri.toString))))

      val producer = Producer[IO, String, String]("test-topic", config)

      producer.send("key", "value").unsafeRunSync must matchPattern { case Right(_) => }
    }

    "send another message to Kafka" in {
      val config = KafkaConfig(BootstrapConfig(Seq(Uri.parse(kafkaContainer.uri.toString))))

      val producer = Producer[IO, String, String]("test-topic", config)

      producer.send("key", "value").unsafeRunSync must matchPattern { case Right(_) => }
    }
  }
}