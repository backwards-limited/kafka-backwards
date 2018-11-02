package com.backwards.kafka.serde

import java.util
import org.apache.kafka.common.errors.SerializationException
import org.apache.kafka.common.serialization.Deserializer

class IntDeserializer extends Deserializer[Int] {
  def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

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

  def close(): Unit = ()
}