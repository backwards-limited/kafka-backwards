package com.backwards.twitter

import java.util.Date
import scala.collection.JavaConverters._
import cats.Id
import cats.effect.IO
import org.apache.kafka.clients.producer.MockProducer
import org.apache.kafka.common.Cluster
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.kafka.Producer
import com.backwards.transform.Transform
import com.danielasfregola.twitter4s.entities.Tweet

class TwitterProducerSpec extends WordSpec with MustMatchers with Transform {
  val topic = "topic"
  val tweet = Tweet(created_at = new Date, id = 6, id_str = "blah", source = "blahblah", text = "something")

  "Twitter producer" should {
    "send a tweet within Id effect" in {
      val mockProducer = new MockProducer[String, Tweet](Cluster.empty(), true, null, null, null)

      val producer = new TwitterProducer[Id](topic) {
        override val producer: Producer[Id, String, Tweet] = new Producer[Id, String, Tweet](topic, mockProducer)
      }

      producer produce tweet

      val Seq(record) = mockProducer.history.asScala

      record must have (
        'topic (topic),
        'value (tweet)
      )
    }

    "send a tweet within IO effect - we have repeated the Id test within another effect mainly for illustration purposes" in {
      val mockProducer = new MockProducer[String, Tweet]

      val producer = new TwitterProducer[IO](topic) {
        override val producer: Producer[IO, String, Tweet] = new Producer[IO, String, Tweet](topic, mockProducer)
      }

      producer produce tweet

      val Seq(record) = mockProducer.history.asScala

      record must have (
        'topic (topic),
        'value (tweet)
      )
    }
  }
}