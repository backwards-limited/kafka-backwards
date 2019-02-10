package com.backwards.kafka

import scala.concurrent.ExecutionContext.Implicits.global
import cats.implicits._
import io.lemonlabs.uri.Uri
import org.scalatest.{MustMatchers, WordSpec}
import org.testcontainers.containers.wait.strategy.Wait
import com.backwards.config.BootstrapConfig
import com.backwards.kafka.serde.Serde
import com.dimafeng.testcontainers.{ForAllTestContainer, GenericContainer}

class KafkaTestContainersSpec extends WordSpec with MustMatchers with ForAllTestContainer with Serde {
  override val container = GenericContainer("spotify/kafka",
    exposedPorts = Seq(2181, 9092),
    //waitStrategy = Wait.forLogMessage("kafka entered RUNNING state", 1),
    waitStrategy = Wait.defaultWaitStrategy(),
    env = Map("ADVERTISED_HOST" -> "0.0.0.0", "ADVERTISED_PORT" -> s"9092")
  )

  "Blah" should {
    "blah" in {
      implicit val config: KafkaConfig = KafkaConfig(BootstrapConfig(Seq(Uri.parse(s"0.0.0.0:" + container.mappedPort(9092)))))

      val producer = new com.backwards.kafka.future.KafkaProducer[String, String]("test-topic")

      producer.send("key", "value") must matchPattern { case Right(_) => }
    }
  }
}