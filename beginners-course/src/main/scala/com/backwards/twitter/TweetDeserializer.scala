package com.backwards.twitter

import java.util
import org.apache.kafka.common.serialization.Deserializer
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._
import com.danielasfregola.twitter4s.entities.Tweet

class TweetDeserializer extends Deserializer[Tweet] {
  def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

  def deserialize(topic: String, data: Array[Byte]): Tweet = {
    implicit val formats: Formats = Serialization formats NoTypeHints

    read[Tweet](new String(java.util.Base64.getDecoder.decode(data)))
  }

  def close(): Unit = ()
}