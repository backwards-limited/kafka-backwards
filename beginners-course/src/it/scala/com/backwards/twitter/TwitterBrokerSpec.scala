package com.backwards.twitter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps
import cats.data.NonEmptyList
import cats.effect.IO
import org.apache.kafka.clients.consumer.ConsumerConfig.{AUTO_OFFSET_RESET_CONFIG, GROUP_ID_CONFIG}
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.producer.RecordMetadata
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.Or
import com.backwards.container._
import com.backwards.kafka._
import com.backwards.kafka.serde.Serde
import com.backwards.logging.Logging

class TwitterBrokerSpec extends WordSpec with MustMatchers with ScalaFutures with ContainerFixture with ForAllContainerLifecycle with Serde.Implicits with Logging {
  lazy val zookeeperContainer = ZookeeperContainer()
  lazy val kafkaContainer = KafkaContainer(zookeeperContainer)

  lazy val container: Container = Container(zookeeperContainer, kafkaContainer)

  override implicit def patienceConfig: PatienceConfig = PatienceConfig(10 seconds, 2 seconds)

  "Twitter brokerage" should {
    "send a tweet to Kafka and consume said tweet" ignore {
      val configuration = Configuration("topic") + (BOOTSTRAP_SERVERS_CONFIG -> kafkaContainer.uri.toString)
      val twitterProducer = Producer[IO, String, String](configuration)

      val tweetKey = "key"
      val tweet = "tweet"

      twitterProducer.send(tweetKey, tweet).unsafeRunSync mustBe a [Right[_, _]]

      val groupId = "twitter-group"
      val autoOffset = "earliest"

      val twitterConsumer = Consumer[IO, String, String](configuration + (GROUP_ID_CONFIG, groupId) + (AUTO_OFFSET_RESET_CONFIG -> autoOffset))

      twitterConsumer.poll().unsafeRunSync mustEqual Seq(tweetKey -> tweet)
    }

    "query tweets from Twitter" ignore {
      val twitterBroker = new TwitterBroker

      whenReady(twitterBroker.query(NonEmptyList.of("scala"))) { ratedData =>
        println(s"Rated Data Count = ${ratedData.data.statuses.size}")
        ratedData.data.statuses.exists(_.text.contains("scala")) mustBe true
      }
    }

    "query tweets from Twitter and then we'll send them to Kafka" in {
      val twitterBroker = new TwitterBroker

      val tweets = whenReady(twitterBroker.query(NonEmptyList.of("scala"))) { ratedData =>
        ratedData.data.statuses.exists(_.text.contains("scala")) mustBe true
        ratedData.data.statuses
      }

      val configuration: Configuration = Configuration("twitter-topic") + (BOOTSTRAP_SERVERS_CONFIG -> kafkaContainer.uri.toString)
      val producer: Producer[IO, String, String] = Producer[IO, String, String](configuration)

      val publishedTweets: Seq[IO[Throwable Or RecordMetadata]] = tweets.map { tweet =>
        producer.send(tweet.id.toString, tweet.text)
      }

      publishedTweets.map(_.unsafeRunSync()).count(_.isRight) mustEqual tweets.size
    }
  }
}