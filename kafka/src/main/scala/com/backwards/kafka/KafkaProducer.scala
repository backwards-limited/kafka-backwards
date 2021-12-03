package com.backwards.kafka

import cats.Monad
import org.apache.kafka.clients.producer.ProducerConfig.{KEY_SERIALIZER_CLASS_CONFIG, VALUE_SERIALIZER_CLASS_CONFIG}
import org.apache.kafka.clients.producer.{ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.Serializer
import com.backwards.Or

// TODO - Maybe this should be F[_]: Sync
abstract class KafkaProducer[F[_]: Monad, K, V](val topic: String)(implicit C: KafkaConfig, K: Serializer[K], V: Serializer[V]) extends WithKafkaConfig {
  val kafkaConfig: KafkaConfig = C + (KEY_SERIALIZER_CLASS_CONFIG -> K.getClass.getName) + (VALUE_SERIALIZER_CLASS_CONFIG -> V.getClass.getName)

  lazy val record: (K, V) => ProducerRecord[K, V] =
    (key, value) => new ProducerRecord[K, V](topic, key, value)

  def send(key: K, value: V): F[Throwable Or RecordMetadata]

  // TODO similar to - def send[V: Identifiable](value: V): F[Throwable Or RecordMetadata]
}