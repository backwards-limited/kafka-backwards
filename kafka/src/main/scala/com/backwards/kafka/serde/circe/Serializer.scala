package com.backwards.kafka.serde.circe

import scala.language.implicitConversions
import io.circe.Encoder
import org.apache.kafka.common.serialization.{Serializer => ApacheKafkaSerializer}
import monix.kafka.{Serializer => MonixKafkaSerializer}

object Serializer extends Serializer

trait Serializer {
  implicit def circeSerializer[T <: Product: Encoder]: MonixKafkaSerializer[T] = {
    lazy val serializer: ApacheKafkaSerializer[T] =
      (topic: String, data: T) => implicitly[Encoder[T]].apply(data).noSpaces.getBytes

    MonixKafkaSerializer[T](
      className = serializer.getClass.getName,
      classType = serializer.getClass,
      constructor = _ => serializer
    )
  }
}