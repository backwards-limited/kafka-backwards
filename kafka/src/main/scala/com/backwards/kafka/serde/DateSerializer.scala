package com.backwards.kafka.serde

import java.util
import java.util.Date
import org.apache.kafka.common.serialization.Serializer

class DateSerializer extends Serializer[Date] {
  def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

  def serialize(topic: String, date: Date): Array[Byte] = date.getTime.toString.getBytes

  def close(): Unit = ()
}