package com.backwards.twitter

import java.util
import java.util.Base64
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._
import com.danielasfregola.twitter4s.entities.Tweet

object TweetSerde {
  implicit val formats: Formats = Serialization formats NoTypeHints

  class TweetSerializer extends Serializer[Tweet] {
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    def serialize(topic: String, data: Tweet): Array[Byte] =
      Base64.getEncoder.encode(write(data).getBytes)

    override def close(): Unit = ()
  }

  class TweetDeserializer extends Deserializer[Tweet] {
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    def deserialize(topic: String, data: Array[Byte]): Tweet =
      read[Tweet](new String(Base64.getDecoder.decode(data)))

    override def close(): Unit = ()
  }

  def apply(): Serde[Tweet] = new Serde[Tweet] {
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    override def close(): Unit = ()

    def serializer(): Serializer[Tweet] = new TweetSerializer

    def deserializer(): Deserializer[Tweet] = new TweetDeserializer
  }
}