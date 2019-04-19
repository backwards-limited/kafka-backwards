package com.backwards.twitter

import java.util.Date
import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.language.postfixOps
import cats.Id
import cats.effect.IO
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.errors.TimeoutException
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.config.BootstrapConfig
import com.backwards.container.{Container, ContainerFixture, ForAllContainerLifecycle}
import com.backwards.kafka.{KafkaConfig, KafkaContainer}
import com.backwards.transform.Transform
import com.backwards.twitter.simple.TwitterProducer
import com.danielasfregola.twitter4s.entities.Tweet

class TwitterProducerSpec extends WordSpec with MustMatchers with ScalaFutures with Transform with ContainerFixture with ForAllContainerLifecycle {
  override implicit def patienceConfig: PatienceConfig = PatienceConfig(10 seconds, 2 seconds)

  lazy val (zookeeperContainer, kafkaContainer) = KafkaContainer()

  lazy val container: Container = Container(zookeeperContainer, kafkaContainer)

  implicit lazy val config: KafkaConfig = KafkaConfig(BootstrapConfig(Seq(kafkaContainer.uri)))

  val topic = "topic"
  val tweet = Tweet(created_at = new Date, id = 6, id_str = "blah", source = "blahblah", text = "something")

  "Twitter producer" should {
    "be correctly configured" in {
      val kafkaConfigProperties = TwitterProducer(topic).kafkaProducer.kafkaConfig.properties

      kafkaConfigProperties("key.serializer") mustBe "org.apache.kafka.common.serialization.StringSerializer"
      kafkaConfigProperties("value.serializer") mustBe "com.backwards.twitter.TweetSerde$TweetSerializer"
    }

    "fail to send a tweet when there are no Kafka brokers available" ignore {
      val twitterProducer = TwitterProducer(topic)

      whenReady(twitterProducer send tweet) { result =>
        result must matchPattern { case Left(_: TimeoutException) => }
      }
    }
  }

  "Twitter producer with Id effect" should {
    "send a tweet" in {
      import com.backwards.twitter.simple.TwitterProducer._

      val kafkaProducer = new com.backwards.kafka.mock.KafkaProducer[Id, String, Tweet](topic)
      val twitterProducer = new TwitterProducer(kafkaProducer)

      twitterProducer.send(tweet) must matchPattern { case Right(_: RecordMetadata) => }

      // The above assertion shows that the producer is happy, but we should double check what is actually on Kafka
      val Seq(record) = kafkaProducer.underlying.history.asScala

      record must have (
        'topic (topic),
        'value (tweet)
      )
    }
  }

  "Twitter producer with IO effect (repeating the Id example showing another effect mainly for illustration purposes)" should {
    "send a tweet" in {
      import com.backwards.twitter.simple.TwitterProducer._

      val kafkaProducer = new com.backwards.kafka.mock.KafkaProducer[IO, String, Tweet](topic)
      val twitterProducer = new TwitterProducer(kafkaProducer)

      twitterProducer.send(tweet).unsafeRunSync must matchPattern { case Right(_: RecordMetadata) => }

      // The above assertion shows that the producer is happy, but we should double check what is actually on Kafka
      val Seq(record) = kafkaProducer.underlying.history.asScala

      record must have (
        'topic (topic),
        'value (tweet)
      )
    }
  }
}