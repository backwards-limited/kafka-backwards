package com.backwards.kafka

import scala.concurrent.Promise
import scala.language.higherKinds
import cats.effect.IO.{fromFuture => ioFromFuture}
import cats.effect.{Effect, IO}
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.Serializer
import com.backwards._
import com.backwards.kafka.config.{KafkaConfig, KafkaConfigOps}

object Producer extends KafkaConfigOps {
  def apply[F[_]: Effect, K, V](topic: String, config: KafkaConfig)(implicit K: Serializer[K], V: Serializer[V]) =
    new Producer[F, K, V](topic, config + keySerializerProperty[K] + valueSerializerProperty[V])
}

class Producer[F[_]: Effect, K, V] private(topic: String, config: KafkaConfig) {
  lazy val producer: KafkaProducer[K, V] = {
    val producer = new KafkaProducer[K, V](config)
    sys addShutdownHook producer.close()
    producer
  }

  lazy val record: (K, V) => ProducerRecord[K, V] =
    (key, value) => new ProducerRecord[K, V](topic, key, value)

  // TODO
  // def send[V: Identifiable]

  def send(key: K, value: V): F[Throwable Or RecordMetadata] = {
    val promise = Promise[Throwable Or RecordMetadata]()

    producer.send(record(key, value), callback(promise))

    implicitly[Effect[F]] liftIO ioFromFuture(IO pure promise.future)
  }

  def callback(promise: Promise[Throwable Or RecordMetadata]): Callback =
    (metadata: RecordMetadata, exception: Exception) =>
      if (exception == null) promise success Right(metadata)
      else promise success Left(exception)
}
