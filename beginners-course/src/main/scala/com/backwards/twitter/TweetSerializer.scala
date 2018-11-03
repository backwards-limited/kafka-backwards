package com.backwards.twitter

import java.util
import org.apache.kafka.common.serialization.Serializer
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._
import com.danielasfregola.twitter4s.entities.Tweet

class TweetSerializer extends Serializer[Tweet] {
  def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

  def serialize(topic: String, data: Tweet): Array[Byte] = {
    implicit val formats: Formats = Serialization formats NoTypeHints

    java.util.Base64.getEncoder.encode(write(data).getBytes)
  }

  def close(): Unit = ()
}