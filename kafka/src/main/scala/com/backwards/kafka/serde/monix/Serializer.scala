package com.backwards.kafka.serde.monix

import org.apache.kafka.common.serialization.{Serializer => ApacheKafkaSerializer}
import monix.kafka.{Serializer => MonixKafkaSerializer}

object Serializer {
  implicit def monixSerializer[T: ApacheKafkaSerializer]: MonixKafkaSerializer[T] = {
    val apacheKafkaSerializer = implicitly[ApacheKafkaSerializer[T]]

    MonixKafkaSerializer[T](
      className = apacheKafkaSerializer.getClass.getName,
      classType = apacheKafkaSerializer.getClass,
      constructor = _ => apacheKafkaSerializer
    )
  }
}