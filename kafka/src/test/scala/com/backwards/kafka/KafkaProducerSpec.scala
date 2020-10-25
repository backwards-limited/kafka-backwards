package com.backwards.kafka

import scala.concurrent.ExecutionContext.Implicits.global
import scala.jdk.CollectionConverters.ListHasAsScala
import cats.Id
import cats.implicits.catsStdInstancesForFuture
import io.lemonlabs.uri.Uri
import org.apache.kafka.clients.producer.ProducerRecord
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import com.backwards.config.BootstrapConfig
import com.backwards.kafka.serde.Serdes
import com.backwards.transform.Transform

class KafkaProducerSpec extends AnyWordSpec with Matchers with ScalaFutures with Serdes with Transform {
  implicit val kafkaConfig: KafkaConfig = KafkaConfig(BootstrapConfig(Seq(Uri.parse("127.0.0.1:9092")))) // load[KafkaConfig]("kafka")

  "Kafka Producer (mocking) with Id as an effect" should {
    "produce a simple message" in {
      val kafkaProducer = new com.backwards.kafka.mock.KafkaProducer[Id, String, String]

      kafkaProducer.send("key", "value").map { _ =>
        val Seq(record: ProducerRecord[String, String]) = kafkaProducer.underlying.history.asScala.toSeq

        record must have (
          'topic (kafkaProducer.topic),
          'value ("value")
        )
      }
    }
  }
}