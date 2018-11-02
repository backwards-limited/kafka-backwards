package com.backwards.kafka.serde

import java.util
import org.apache.kafka.common.serialization.Serializer

class IntSerializer extends Serializer[Int] {
  def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

  def serialize(topic: String, data: Int): Array[Byte] =
    Array[Byte]((data >>> 24).toByte, (data >>> 16).toByte, (data >>> 8).toByte, data.byteValue)

  def close(): Unit = ()
}