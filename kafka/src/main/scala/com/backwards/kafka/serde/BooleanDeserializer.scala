package com.backwards.kafka.serde

import java.util
import org.apache.kafka.common.serialization.Deserializer

class BooleanDeserializer extends Deserializer[Boolean] {
  def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

  def deserialize(topic: String, data: Array[Byte]): Boolean = new String(data) == "true"

  def close(): Unit = ()
}