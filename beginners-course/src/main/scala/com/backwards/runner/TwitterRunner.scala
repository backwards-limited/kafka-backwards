package com.backwards.runner

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps
import cats.data.NonEmptyList
import cats.effect.IO
import org.apache.kafka.clients.consumer.ConsumerConfig.{AUTO_OFFSET_RESET_CONFIG, GROUP_ID_CONFIG}
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import com.backwards.config._
import com.backwards.elasticsearch.ElasticSearchBroker
import com.backwards.kafka.serde.Serde
import com.backwards.kafka.{Configuration, Consumer, Producer}
import com.backwards.logging.Logging
import com.backwards.twitter.TwitterBroker
import com.danielasfregola.twitter4s.entities.Tweet

/**
  * Demo application which shows the following:
  *   - Query Twitter for "Scala" tweets using [[https://github.com/DanielaSfregola/twitter4s twitter4s]]
  *   - Send tweets to Kafka using [[com.backwards.kafka.Producer Producer]]
  *   - Receive said tweets from Kafka using [[com.backwards.kafka.Consumer Consumer]]
  *   - Finally each received tweet is added to Elasticsearch using [[https://github.com/bizreach/elastic-scala-httpclient elastic-scala-httpclient]]
  */
/*
object TwitterRunner extends App with Serde.Implicits with Logging {
  val twitterBroker = new TwitterBroker
  val tweets: Future[Seq[Tweet]] = twitterBroker.query(NonEmptyList.of("scala")).map(_.data.statuses)

  // TODO - Aquire this from application.conf/pureconfig
  val configuration: Configuration = Configuration("twitter-topic") + (BOOTSTRAP_SERVERS_CONFIG -> kafkaConfig.bootstrapServers)

  val producer: Producer[IO, String, String] = Producer[IO, String, String](configuration)

  val publishedTweets: Future[Unit] = tweets.map {
    _.map { tweet =>
      producer.send(tweet.id.toString, tweet.text)
    }.map(_.unsafeRunSync).foreach {
      case Right(r) => info(s"Published successfully: $r")
      case Left(t) => error("Publication Error", t)
    }
  }

  val consumer = Consumer[IO, String, String](configuration + (GROUP_ID_CONFIG, "twitter-group") + (AUTO_OFFSET_RESET_CONFIG -> "earliest"))

  val consumedTweets: Seq[(String, String)] = consumer.poll().unsafeRunSync
  info(s"Consumed Tweets:\n${consumedTweets.mkString("\n")}")

  val elasticsearchBroker = new ElasticSearchBroker
}*/
