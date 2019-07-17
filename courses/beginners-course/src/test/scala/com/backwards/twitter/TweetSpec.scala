package com.backwards.twitter

import java.util.Date
import org.apache.commons.lang3.SerializationUtils
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.kafka.serde.{Deserialize, Serialize}
import com.backwards.twitter.TweetSerde.{TweetDeserializer, TweetSerializer}
import com.danielasfregola.twitter4s.entities._

class TweetSpec extends WordSpec with MustMatchers with Serialize with Deserialize {
  val tweet = Tweet(created_at = new Date, id = 6, id_str = "blah", source = "blahblah", text = "something")

  "Tweet" should {
    "serialized/deserialized directly" in {
      val bytes: Array[Byte] = SerializationUtils serialize tweet

      SerializationUtils.deserialize[Tweet](bytes) mustEqual tweet
    }

    "serialize and deserialize implicitly" in {
      implicit val tweetSerializer: TweetSerializer = new TweetSerializer
      implicit val tweetDeserializer: TweetDeserializer = new TweetDeserializer

      val topic = "topic"

      val bytes = serialize[Tweet](topic, tweet)

      deserialize[Tweet](topic, bytes) mustEqual tweet
    }
  }
}