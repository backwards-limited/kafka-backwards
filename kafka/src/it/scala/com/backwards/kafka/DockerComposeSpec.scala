package com.backwards.kafka

import java.time.Instant
import scala.language.postfixOps
import better.files._
import cats.Id
import cats.implicits._
import io.lemonlabs.uri.Uri
import org.apache.kafka.clients.consumer.ConsumerConfig.{AUTO_OFFSET_RESET_CONFIG, GROUP_ID_CONFIG}
import org.apache.kafka.clients.producer.RecordMetadata
import org.scalatest.{AsyncWordSpec, MustMatchers}
import com.backwards.config.BootstrapConfig
import com.backwards.docker.DockerCompose.ServiceName
import com.backwards.docker.{DockerCompose, DockerComposeFixture}
import com.backwards.kafka.serde.Serdes

class DockerComposeSpec extends AsyncWordSpec with MustMatchers with Serdes with DockerComposeFixture {
  val dockerCompose: DockerCompose =
    DockerCompose("kafka", Seq(File(Resource.getUrl("docker-compose.yml"))))

  val now: Instant = Instant.now

  "Kafka producer" should {
    "send a message to Kafka" in {
      val kafkaPort = dockerCompose.containerMappedPort(ServiceName("kafka"), 9092)

      implicit lazy val config: KafkaConfig = KafkaConfig(BootstrapConfig(Seq(Uri.parse(s"http://localhost:$kafkaPort"))))

      val producer = new com.backwards.kafka.future.KafkaProducer[String, String]("test-topic")

      producer.send("key", now.toString).collect {
        case Right(_: RecordMetadata) =>
          val consumer = Consumer[Id, String, String]("test-topic", config + (GROUP_ID_CONFIG -> "test-topic-group") + (AUTO_OFFSET_RESET_CONFIG -> "earliest"))
          println("===> GOT: " + consumer.poll())

          consumer.underlying.close()
          producer.underlying.close()

          succeed
      }
    }

    /*"send another message to Kafka" in {
      val producer = new com.backwards.kafka.future.KafkaProducer[String, String]("test-topic")

      producer.send("key", now.plusSeconds(1).toString).collect {
        case Right(_: RecordMetadata) => succeed
      }
    }*/
  }
}