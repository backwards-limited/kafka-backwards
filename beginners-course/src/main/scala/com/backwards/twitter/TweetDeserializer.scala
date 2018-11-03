package com.backwards.twitter

import java.util
import org.apache.commons.lang3.SerializationUtils
import org.apache.kafka.common.serialization.Deserializer
import com.danielasfregola.twitter4s.entities.Tweet

class TweetDeserializer extends Deserializer[Tweet] {
  def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

  def deserialize(topic: String, data: Array[Byte]): Tweet = SerializationUtils deserialize data

  def close(): Unit = ()
}