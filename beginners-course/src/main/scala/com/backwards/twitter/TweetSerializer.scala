package com.backwards.twitter

import java.util
import org.apache.kafka.common.serialization.Serializer
import com.danielasfregola.twitter4s.entities.Tweet
import org.apache.commons.lang3.SerializationUtils

class TweetSerializer extends Serializer[Tweet] {
  def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

  def serialize(topic: String, data: Tweet): Array[Byte] = SerializationUtils serialize data

  def close(): Unit = ()
}