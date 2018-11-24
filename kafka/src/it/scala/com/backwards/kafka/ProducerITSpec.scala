package com.backwards.kafka

import scala.concurrent.ExecutionContext.Implicits.global
import cats.implicits._
import io.lemonlabs.uri.Uri
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.config.BootstrapConfig
import com.backwards.container.{Container, ContainerFixture}
import com.backwards.kafka.serde.Serde

trait ProducerITSpec extends WordSpec with MustMatchers with ScalaFutures with Serde {
  this: ContainerFixture =>

  lazy val zookeeperContainer = ZookeeperContainer()
  lazy val kafkaContainer = KafkaContainer(zookeeperContainer)

  lazy val container: Container = Container(zookeeperContainer, kafkaContainer)

  implicit val config: KafkaConfig = KafkaConfig(BootstrapConfig(Seq(Uri.parse(kafkaContainer.uri.toString))))

  "Kafka producer" should {
    "send a message to Kafka" in {
      val producer = new com.backwards.kafka.future.KafkaProducer[String, String]("test-topic")

      producer.send("key", "value") must matchPattern { case Right(_) => }
    }

    "send another message to Kafka" in {
      val producer = new com.backwards.kafka.future.KafkaProducer[String, String]("test-topic")

      producer.send("key", "value") must matchPattern { case Right(_) => }
    }
  }
}