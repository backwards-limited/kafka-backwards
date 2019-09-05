package com.backwards.kafka

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.higherKinds
import cats.Id
import cats.implicits.catsStdInstancesForFuture
import org.apache.kafka.clients.producer.RecordMetadata
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.Or
import com.backwards.config.Config._
import com.backwards.kafka.serde.Serdes
import com.backwards.transform.Transform

class KafkaProducerSpec extends WordSpec with MustMatchers with ScalaFutures with Serdes with Transform {
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

  "" should {
    "" in {

      val kafkaProducer = new com.backwards.kafka.future.KafkaProducer2[String, String]("test-topic")


      val v: Future[Throwable Or RecordMetadata] = kafkaProducer.send("key", "value").run()

      /*whenReady(v) { result =>
        result must matchPattern { case Left(_: TimeoutException) => }
      }*/

      /*val blah: Throwable Or RecordMetadata => Future[String] = {
        case Left(t) => ???
        case Right(r) => ???
      }*/

      val blah: Throwable Or RecordMetadata => Future[Boolean] = _.fold(_ => Future.successful(true), _ => Future.failed(new Exception("Incorrectly succeeded")))

      whenReady(v.flatMap(blah)) { result =>
        result mustBe true
      }
    }
  }
}