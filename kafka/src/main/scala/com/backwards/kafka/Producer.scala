package com.backwards.kafka

import scala.concurrent.{Future, Promise}
import scala.language.higherKinds
import cats.{Monad, _}
import cats.implicits._
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.Serializer
import com.backwards._
import com.backwards.kafka.config.{KafkaConfig, KafkaConfigOps}
import com.backwards.transform.Transform

object Producer extends KafkaConfigOps {
  def apply[F[_]: Monad, K, V](topic: String, config: KafkaConfig)(implicit K: Serializer[K], V: Serializer[V]) =
    new Producer[F, K, V](topic, config + keySerializerProperty[K] + valueSerializerProperty[V])
}

class Producer[F[_]: Monad, K, V] private(topic: String, config: KafkaConfig) extends Transform {
  lazy val producer: KafkaProducer[K, V] = {
    val producer = new KafkaProducer[K, V](config)
    sys addShutdownHook producer.close()
    producer
  }

  lazy val record: (K, V) => ProducerRecord[K, V] =
    (key, value) => new ProducerRecord[K, V](topic, key, value)

  // TODO similar to - def send[V: Identifiable]

  def send(key: K, value: V)(implicit transform: Future ~> F): F[Throwable Or RecordMetadata] = {
    val promise = Promise[Throwable Or RecordMetadata]()

    producer.send(record(key, value), callback(promise))

    promise.future.liftTo[F]
  }

  def callback(promise: Promise[Throwable Or RecordMetadata]): Callback =
    (metadata: RecordMetadata, exception: Exception) =>
      if (exception == null) promise success Right(metadata)
      else promise success Left(exception)
}