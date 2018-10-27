package com.backwards.kafka

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import cats.{Id, ~>}
import io.lemonlabs.uri.Uri
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.container.{Container, ContainerFixture, ForAllContainerLifecycle}
import com.backwards.kafka.config.{BootstrapConfig, KafkaConfig}
import com.backwards.kafka.serde.Serde

// TODO - Remove this PoC once ProducerSpec is complete, that shows the use of ID as the effect monad and also has proven "laws"
class ProducerIdPocITSpec extends WordSpec with MustMatchers with ContainerFixture with ForAllContainerLifecycle with Serde.Implicits {
  implicit val `future ~> Id`: ~>[Future, Id] = new (Future ~> Id) {
    override def apply[A](future: Future[A]): Id[A] =
      Await.result(future, 30 seconds)
  }

  lazy val zookeeperContainer = ZookeeperContainer()
  lazy val kafkaContainer = KafkaContainer(zookeeperContainer)

  lazy val container: Container = Container(zookeeperContainer, kafkaContainer)

  "Kafka producer" should {
    "send a message to Kafka" in {
      val config = KafkaConfig(BootstrapConfig(Seq(Uri.parse(kafkaContainer.uri.toString))))

      val producer = Producer[Id, String, String]("test-topic", config)

      producer.send("key", "value") must matchPattern { case Right(_) => }
    }
  }
}