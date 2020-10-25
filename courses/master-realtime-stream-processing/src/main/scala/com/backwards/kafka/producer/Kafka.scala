package com.backwards.kafka.producer

import java.util.Properties
import io.circe.Encoder
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.Serializer
import io.github.azhur.kafkaserdecirce.CirceSupport.toSerializer

object Kafka {
  def producer[K, V](properties: Properties)(implicit K: Serializer[K], V: Serializer[V]): KafkaProducer[K, V] =
    new KafkaProducer[K, V](properties, K, V)

  object circe {
    def producer[K >: Null, V >: Null](properties: Properties)(implicit K: Encoder[K], V: Encoder[V]): KafkaProducer[K, V] =
      new KafkaProducer[K, V](properties, toSerializer(K), toSerializer(V))
  }
}