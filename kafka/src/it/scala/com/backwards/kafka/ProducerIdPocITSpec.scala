package com.backwards.kafka

import scala.language.postfixOps
import cats.Id
import io.lemonlabs.uri.Uri
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.config.BootstrapConfig
import com.backwards.container.{Container, ContainerFixture, ForAllContainerLifecycle}
import com.backwards.kafka.config.KafkaConfig
import com.backwards.kafka.serde.Serde
import com.backwards.transform.Transform

// TODO - Remove this PoC once ProducerSpec is complete, that shows the use of ID as the effect monad and also has proven "laws"
class ProducerIdPocITSpec extends WordSpec with MustMatchers with ContainerFixture with ForAllContainerLifecycle with Serde with Transform {
  lazy val zookeeperContainer = ZookeeperContainer()
  lazy val kafkaContainer = KafkaContainer(zookeeperContainer)

  lazy val container: Container = Container(zookeeperContainer, kafkaContainer)

  "Kafka producer" should {
    "send a message to Kafka" in {
      val config = KafkaConfig(BootstrapConfig(Seq(Uri parse kafkaContainer.uri.toString)))

      val producer = Producer[Id, String, String]("test-topic", config)

      producer.send("key", "value") must matchPattern { case Right(_) => }
    }
  }
}