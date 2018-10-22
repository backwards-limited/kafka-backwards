package com.backwards.runner

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.{higherKinds, postfixOps}
import cats.data.NonEmptyList
import cats.effect.{Effect, IO}
import cats.implicits._
import org.apache.kafka.clients.consumer.ConsumerConfig.{AUTO_OFFSET_RESET_CONFIG, GROUP_ID_CONFIG}
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.common.serialization.{Deserializer, Serializer}
import com.backwards.config._
import com.backwards.elasticsearch.ElasticSearchBroker
import com.backwards.kafka.serde.Serde
import com.backwards.kafka.{Configuration, Consumer}
import com.backwards.logging.Logging
import com.backwards.twitter.{TwitterBroker, TwitterProducer}
import scala .concurrent.duration._

/**
  * Demo application which shows the following:
  *   - Query Twitter for "Scala" tweets using [[https://github.com/DanielaSfregola/twitter4s twitter4s]]
  *   - Send tweets to Kafka using [[com.backwards.kafka.Producer Producer]]
  *   - Receive said tweets from Kafka using [[com.backwards.kafka.Consumer Consumer]]
  *   - Finally each received tweet is added to Elasticsearch using [[https://github.com/bizreach/elastic-scala-httpclient elastic-scala-httpclient]]
  */
object TwitterRunner extends App with Serde.Implicits with Logging {
  // TODO - Aquire this from application.conf/pureconfig
  val configuration: Configuration = Configuration("twitter-topic") + (BOOTSTRAP_SERVERS_CONFIG -> kafkaConfig.bootstrapServers)

  val twitterProducer = TwitterProducer[IO](configuration)

  val twitterBroker = new TwitterBroker
  twitterBroker.track(NonEmptyList.of("scala"))(twitterProducer.produce(_).unsafeRunSync)

  val twitterConsumer = TwitterConsumer[IO](configuration)

  val consumeTweets: Throwable Either Seq[(String, String)] => IO[Unit] = {
    case Left(t) =>
      IO(t.printStackTrace())

    case Right(tweets) =>
      IO(info(s"Consumed Tweets:\n${tweets.mkString("\n")}")).map { _ =>
        twitterConsumer.consume().runAsync(consumeTweets).unsafeRunSync()
      }
  }

  twitterConsumer.consume().runAsync(consumeTweets).unsafeRunSync

  /////////////
  // TODO - Extract into a TwitterConsumer
  /*val consumer = Consumer[IO, String, String](configuration + (GROUP_ID_CONFIG, "twitter-group") + (AUTO_OFFSET_RESET_CONFIG -> "earliest"))

  val consumedTweets: Seq[(String, String)] = consumer.poll().unsafeRunSync
  info(s"Consumed Tweets:\n${consumedTweets.mkString("\n")}")

  // TODO - Actually use instead of the hardcoded PoC inside the following class
  val elasticsearchBroker = new ElasticSearchBroker*/
}

object TwitterConsumer {
  def apply[F[_]](configuration: Configuration)(implicit F: Effect[F], K: Deserializer[String], V: Deserializer[String]) =
    new TwitterConsumer[F](configuration + (GROUP_ID_CONFIG, "twitter-group") + (AUTO_OFFSET_RESET_CONFIG -> "earliest"))(F, K, V)
}

class TwitterConsumer[F[_]] private(configuration: Configuration)(implicit F: Effect[F], K: Deserializer[String], V: Deserializer[String]) {
  val consumer: Consumer[F, String, String] = Consumer[F, String, String](configuration)(F, K, V)

  def consume(): F[Seq[(String, String)]] = {
    consumer.poll(30 seconds)
  }
}