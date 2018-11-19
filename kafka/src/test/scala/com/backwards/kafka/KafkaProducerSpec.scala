package com.backwards.kafka

import scala.collection.JavaConverters._
import scala.language.higherKinds
import cats.Id
import cats.implicits.catsStdInstancesForFuture
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.config.Config._
import com.backwards.kafka.serde.Serde
import com.backwards.transform.Transform

class KafkaProducerSpec extends WordSpec with MustMatchers with Serde with Transform {
  implicit val kafkaConfig: KafkaConfig = load[KafkaConfig]("kafka")

  "Kafka Producer (mocking) with Id as an effect" should {
    "produce a simple message" in {
      val kafkaProducer = new com.backwards.kafka.mock.KafkaProducer[Id, String, String]

      kafkaProducer.send("key", "value").map { _ =>
        val Seq(record) = kafkaProducer.underlying.history.asScala

        record must have (
          'topic (kafkaProducer.topic),
          'value ("value")
        )
      }
    }
  }
}