package com.backwards.kafka.mock

import scala.concurrent.{Future, Promise}
import scala.language.higherKinds
import cats.{Monad, ~>}
import org.apache.kafka.clients.producer.{Callback, MockProducer, RecordMetadata}
import org.apache.kafka.common.Cluster
import org.apache.kafka.common.serialization.Serializer
import com.backwards.Or
import com.backwards.kafka.KafkaConfig
import com.backwards.transform.Transform

class KafkaProducer[F[_]: Monad, K, V](override val topic: String = "topic")(implicit C: KafkaConfig, K: Serializer[K], V: Serializer[V], transform: Future ~> F)
    extends com.backwards.kafka.KafkaProducer[F, K, V](topic) with Transform {

  lazy val underlying = new MockProducer[K, V](Cluster.empty(), true, null, null, null)

  def send(key: K, value: V): F[Throwable Or RecordMetadata] = {
    val promise = Promise[Throwable Or RecordMetadata]()

    underlying.send(record(key, value), callback(promise))

    promise.future.liftTo[F]
    // Monad[F].pure((new Exception).asLeft)
  }

  def callback(promise: Promise[Throwable Or RecordMetadata]): Callback =
    (metadata: RecordMetadata, exception: Exception) =>
      if (exception == null) promise success Right(metadata)
      else promise success Left(exception)
}