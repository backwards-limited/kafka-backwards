package com.backwards.kafka.serde.circe

import scala.language.implicitConversions
import io.circe.Encoder
import org.apache.kafka.common.serialization.{Serializer => ApacheKafkaSerializer}

object Serializer {
  implicit def circeSerializer[T <: Product: Encoder]: ApacheKafkaSerializer[T] =
    (topic: String, data: T) => implicitly[Encoder[T]].apply(data).noSpaces.getBytes
}