package com.backwards.kafka.serde.circe

import io.circe.Decoder
import io.circe.parser.decode
import org.apache.kafka.common.serialization.{Deserializer => ApacheKafkaDeserializer}
import com.backwards.kafka.serde.DeserializationException

object Deserializer {
  implicit def circeDeserializer[T <: Product: Decoder]: ApacheKafkaDeserializer[T] =
    (topic: String, data: Array[Byte]) => Option(data).fold(null.asInstanceOf[T]) { data =>
      decode[T](new String(data)).fold(error => throw DeserializationException(error), identity)
    }
}