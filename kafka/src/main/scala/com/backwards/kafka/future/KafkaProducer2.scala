package com.backwards.kafka.future

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.concurrent.ExecutionContext
import cats.Monad
import org.apache.kafka.clients.producer.{Callback, RecordMetadata, KafkaProducer => KafkaProducerUnderlying}
import org.apache.kafka.common.serialization.Serializer
import com.backwards.Or
import com.backwards.future.LazyFuture
import com.backwards.kafka.KafkaConfig

class KafkaProducer2[K, V](topic: String)(implicit C: KafkaConfig, M: Monad[LazyFuture], K: Serializer[K], V: Serializer[V]/*, EC: ExecutionContext*/)
    extends com.backwards.kafka.KafkaProducer[LazyFuture, K, V](topic) {

  lazy val underlying: KafkaProducerUnderlying[K, V] = {
    val underlying = new KafkaProducerUnderlying[K, V](kafkaConfig)
    sys addShutdownHook underlying.close()
    underlying
  }

  def send(key: K, value: V): LazyFuture[Throwable Or RecordMetadata] = _ => {
    val promise = Promise[Throwable Or RecordMetadata]()

    underlying.send(record(key, value), callback(promise))

    promise.future
  }

  def callback(promise: Promise[Throwable Or RecordMetadata]): Callback =
    (metadata: RecordMetadata, exception: Exception) =>
      if (exception == null) promise success Right(metadata)
      else promise success Left(exception)
}