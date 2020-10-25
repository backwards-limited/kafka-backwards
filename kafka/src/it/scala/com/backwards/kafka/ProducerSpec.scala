package com.backwards.kafka

import scala.concurrent.ExecutionContext.Implicits.global
import cats.implicits._
import io.lemonlabs.uri.Uri
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import com.backwards.config.BootstrapConfig
import com.backwards.container.{Container, ContainerFixture}
import com.backwards.kafka.serde.Serdes

// TODO - WIP
// TODO - The examples are all ignored because of underlying issues with "test containers".
// TODO - Instead use com.backwards.docker.DockerComposeFixture with com.backwards.docker.DockerCompose
trait ProducerSpec extends AnyWordSpec with Matchers with ScalaFutures with Serdes {
  this: ContainerFixture =>

  lazy val zookeeperContainer: ZookeeperContainer = ZookeeperContainer()
  lazy val kafkaContainer: KafkaContainer = KafkaContainer(zookeeperContainer)

  lazy val container: Container = Container(zookeeperContainer, kafkaContainer)

  implicit lazy val config: KafkaConfig = KafkaConfig(BootstrapConfig(Seq(Uri.parse(kafkaContainer.uri.toString))))

  "Kafka producer" should {
    "send a message to Kafka" ignore {
      val producer = new com.backwards.kafka.future.KafkaProducer[String, String]("test-topic")

      producer.send("key", "value") must matchPattern { case Right(_) => }
    }

    "send another message to Kafka" ignore {
      val producer = new com.backwards.kafka.future.KafkaProducer[String, String]("test-topic")

      producer.send("key", "value") must matchPattern { case Right(_) => }
    }
  }
}