package com.backwards.kafka.serde.monix

import monix.kafka.{Deserializer => MonixKafkaDeserializer}
import org.apache.kafka.common.serialization.{Deserializer => ApacheKafkaDeserializer}

object Deserializer {
  implicit def monixDeserializer[T: ApacheKafkaDeserializer]: MonixKafkaDeserializer[T] = {
    val apacheKafkaDeserializer = implicitly[ApacheKafkaDeserializer[T]]

    MonixKafkaDeserializer[T](
      className = apacheKafkaDeserializer.getClass.getName,
      classType = apacheKafkaDeserializer.getClass,
      constructor = _ => apacheKafkaDeserializer
    )
  }
}