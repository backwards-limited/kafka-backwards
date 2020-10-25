package com.backwards.kafka.serde

import java.util
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.{Deserializer, Serializer}

object IntSerde {
  class IntSerializer extends Serializer[Int] {
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    def serialize(topic: String, data: Int): Array[Byte] =
      Array[Byte]((data >>> 24).toByte, (data >>> 16).toByte, (data >>> 8).toByte, data.byteValue)

    override def close(): Unit = ()
  }

  class IntDeserializer extends Deserializer[Int] {
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    def deserialize(topic: String, data: Array[Byte]): Int = {
      if (data.length != 4) throw new SerializationException("Size of data received by IntegerDeserializer is not 4")

      // TODO - Get rid of "var" - the following was a C & P from Java
      var value = 0

      for (b <- data) {
        value <<= 8
        value |= b & 0xFF
      }

      value
    }

    override def close(): Unit = ()
  }
}