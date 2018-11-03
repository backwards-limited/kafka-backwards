package com.backwards.kafka.serde

import java.util
import org.apache.kafka.common.serialization.Serializer

class BooleanSerializer extends Serializer[Boolean] {
  def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

  def serialize(topic: String, data: Boolean): Array[Byte] = data.toString.getBytes

  def close(): Unit = ()
}