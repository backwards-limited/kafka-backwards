package com.backwards.twitter

import java.time.Instant
import org.apache.commons.lang3.SerializationUtils
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import com.backwards.kafka.serde.{Deserialize, Serialize}
import com.backwards.twitter.TweetSerde.{TweetDeserializer, TweetSerializer}
import com.danielasfregola.twitter4s.entities._

class TweetSpec extends AnyWordSpec with Matchers with Serialize with Deserialize {
  val tweet: Tweet = Tweet(created_at = Instant.now, id = 6, id_str = "blah", source = "blahblah", text = "something")

  "Tweet" should {
    "serialized/deserialized directly" in {
      val bytes: Array[Byte] = SerializationUtils serialize tweet

      SerializationUtils.deserialize[Tweet](bytes) mustEqual tweet
    }

    // TODO - Broken after upgrading twitter4s
    "serialize and deserialize implicitly" ignore {
      implicit val tweetSerializer: TweetSerializer = new TweetSerializer
      implicit val tweetDeserializer: TweetDeserializer = new TweetDeserializer

      val topic = "topic"

      val bytes = serialize[Tweet](topic, tweet)

      deserialize[Tweet](topic, bytes) mustEqual tweet
    }
  }
}