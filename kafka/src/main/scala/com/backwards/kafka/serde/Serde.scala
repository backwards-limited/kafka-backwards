package com.backwards.kafka.serde

import org.apache.kafka.common.serialization.{Deserializer, Serializer, Serde => ApacheKafkaSerde, Serdes => ApacheKafkaSerdes}

object Serde {
  implicit def serde[T: Serializer: Deserializer]: ApacheKafkaSerde[T] = new ApacheKafkaSerdes.WrapperSerde(
    implicitly[Serializer[T]],
    implicitly[Deserializer[T]]
  )
}