package com.backwards.kafka

import scala.concurrent.{Future, Promise}
import scala.language.higherKinds
import cats.implicits._
import cats.{Monad, _}
import org.apache.kafka.clients.producer.{Callback, ProducerRecord, RecordMetadata, KafkaProducer => KafkaProducerImpl, Producer => KafkaProducer}
import org.apache.kafka.common.serialization.Serializer
import com.backwards._
import com.backwards.kafka.config.{KafkaConfig, KafkaConfigOps}
import com.backwards.transform.Transform

object Producer extends KafkaConfigOps {
  def apply[F[_]: Monad, K, V](topic: String, config: KafkaConfig)(implicit K: Serializer[K], V: Serializer[V]): Producer[F, K, V] = {
    lazy val kafkaProducer: KafkaProducer[K, V] = {
      val kafkaProducer = new KafkaProducerImpl[K, V](config + keySerializerProperty[K] + valueSerializerProperty[V])
      sys addShutdownHook kafkaProducer.close()
      kafkaProducer
    }

    new Producer[F, K, V](topic, kafkaProducer)
  }
}

class Producer[F[_]: Monad, K, V](topic: String, kafkaProducer: => KafkaProducer[K, V]) extends Transform {
  lazy val record: (K, V) => ProducerRecord[K, V] =
    (key, value) => new ProducerRecord[K, V](topic, key, value)

  // TODO similar to - def send[V: Identifiable]

  def send(key: K, value: V)(implicit transform: Future ~> F): F[Throwable Or RecordMetadata] = {
    val promise = Promise[Throwable Or RecordMetadata]()

    kafkaProducer.send(record(key, value), callback(promise))

    promise.future.liftTo[F]
  }

  def callback(promise: Promise[Throwable Or RecordMetadata]): Callback =
    (metadata: RecordMetadata, exception: Exception) =>
      if (exception == null) promise success Right(metadata)
      else promise success Left(exception)
}