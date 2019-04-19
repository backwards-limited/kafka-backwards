package com.backwards.kafka

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps
import cats.implicits._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.config.BootstrapConfig
import com.backwards.container.{Container, _}
import com.backwards.kafka.serde.Serde

class KafkaTestContainersSpec extends WordSpec with MustMatchers with ScalaFutures with ContainerFixture with ForAllContainerLifecycle with Serde {
  override implicit def patienceConfig: PatienceConfig = PatienceConfig(10 seconds, 2 seconds)

  lazy val (zookeeperContainer, kafkaContainer) = KafkaContainer()

  lazy val container: Container = Container(zookeeperContainer, kafkaContainer)

  implicit lazy val config: KafkaConfig = KafkaConfig(BootstrapConfig(Seq(kafkaContainer.uri)))

  "Blah" should {
    "blah" in {
      val producer = new com.backwards.kafka.future.KafkaProducer[String, String]("test-topic")

      whenReady(producer.send("key", "value")) { result =>
        result must matchPattern { case Right(_) => }
      }
    }
  }
}