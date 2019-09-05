package com.backwards.kafka.serde

import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer, Serdes => ApacheKafkaSerdes}

object Serde {
  implicit def serde[T: Serializer: Deserializer]: Serde[T] = new ApacheKafkaSerdes.WrapperSerde(
    implicitly[Serializer[T]],
    implicitly[Deserializer[T]]
  )
}