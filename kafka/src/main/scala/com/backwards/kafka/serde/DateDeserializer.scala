package com.backwards.kafka.serde

import java.util
import java.util.Date
import org.apache.kafka.common.serialization.Deserializer

class DateDeserializer extends Deserializer[Date] {
  def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

  def deserialize(topic: String, data: Array[Byte]): Date = new Date(new String(data).toLong)

  def close(): Unit = ()
}