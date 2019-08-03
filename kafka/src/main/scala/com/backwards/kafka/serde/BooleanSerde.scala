package com.backwards.kafka.serde

import java.util
import org.apache.kafka.common.serialization.{Deserializer, Serializer}

object BooleanSerde {
  class BooleanSerializer extends Serializer[Boolean] {
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    def serialize(topic: String, data: Boolean): Array[Byte] = data.toString.getBytes

    override def close(): Unit = ()
  }

  class BooleanDeserializer extends Deserializer[Boolean] {
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    def deserialize(topic: String, data: Array[Byte]): Boolean = new String(data) == "true"

    override def close(): Unit = ()
  }
}