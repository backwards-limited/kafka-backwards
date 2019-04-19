package com.backwards.twitter

import java.util.Date
import java.{lang, util}
import scala.collection.JavaConverters._
import cats.Id
import cats.effect.IO
import org.apache.kafka.clients.consumer.{ConsumerRecord, MockConsumer, OffsetResetStrategy}
import org.apache.kafka.common.TopicPartition
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.config.BootstrapConfig
import com.backwards.container._
import com.backwards.kafka.{Consumer, KafkaConfig, KafkaContainer}
import com.backwards.twitter.simple.TwitterConsumer
import com.danielasfregola.twitter4s.entities.Tweet

class TwitterConsumerSpec extends WordSpec with MustMatchers with ContainerFixture with ForAllContainerLifecycle {
  lazy val (zookeeperContainer, kafkaContainer) = KafkaContainer()

  lazy val container: Container = Container(zookeeperContainer, kafkaContainer)

  implicit lazy val config: KafkaConfig = KafkaConfig(BootstrapConfig(Seq(kafkaContainer.uri)))

  trait Context {
    val topic = "topic"
    val tweet = Tweet(created_at = new Date, id = 6, id_str = "blah", source = "blahblah", text = "something")

    val topicPartition = new TopicPartition(topic, 0)

    val mockConsumer = new MockConsumer[String, Tweet](OffsetResetStrategy.EARLIEST)

    mockConsumer.assign(util.Arrays.asList(topicPartition))

    val beginningOffsets: Map[TopicPartition, lang.Long] =
      Map(topicPartition -> java.lang.Long.valueOf(0))

    mockConsumer.updateBeginningOffsets(beginningOffsets.asJava)
  }

  "Twitter consumer" should {
    "receive tweets within Id effect" in new Context {
      val consumer: TwitterConsumer[Id] = new TwitterConsumer[Id](topic) {
        override val consumer: Consumer[Id, String, Tweet] = new Consumer[Id, String, Tweet](topic, mockConsumer)
      }

      val key1 = "key1"
      mockConsumer addRecord new ConsumerRecord[String, Tweet](topic, 0, 0, key1, tweet)

      consumer.consume mustEqual Seq(key1 -> tweet)

      val key2 = "key2"
      mockConsumer addRecord new ConsumerRecord[String, Tweet](topic, 0, 1, key2, tweet)

      consumer.consume mustEqual Seq(key2 -> tweet)
    }

    "receive tweets within IO effect - duplicate of previous example within Id effect for illustration purposes" in new Context {
      val consumer: TwitterConsumer[IO] = new TwitterConsumer[IO](topic) {
        override val consumer: Consumer[IO, String, Tweet] = new Consumer[IO, String, Tweet](topic, mockConsumer)
      }

      val key1 = "key1"
      mockConsumer addRecord new ConsumerRecord[String, Tweet](topic, 0, 0, key1, tweet)

      consumer.consume.unsafeRunSync.toList mustEqual Seq(key1 -> tweet)

      val key2 = "key2"
      mockConsumer addRecord new ConsumerRecord[String, Tweet](topic, 0, 1, key2, tweet)

      consumer.consume.unsafeRunSync.toList mustEqual Seq(key2 -> tweet)
    }
  }
}