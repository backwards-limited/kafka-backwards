package com.backwards.kafka.serde

import java.time.Instant
import java.util
import org.apache.kafka.common.serialization.{Deserializer, Serializer}

object InstantSerde {
  class InstantSerializer extends Serializer[Instant] {
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    def serialize(topic: String, instant: Instant): Array[Byte] = instant.toEpochMilli.toString.getBytes

    override def close(): Unit = ()
  }

  class InstantDeserializer extends Deserializer[Instant] {
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    def deserialize(topic: String, data: Array[Byte]): Instant = Instant.ofEpochMilli(new String(data).toLong)

    override def close(): Unit = ()
  }
}