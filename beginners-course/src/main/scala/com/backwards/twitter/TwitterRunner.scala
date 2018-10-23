package com.backwards.twitter

import cats.data.NonEmptyList
import cats.effect.IO
import com.backwards.logging.Logging

/**
  * Demo application which shows the following:
  *   - Query Twitter for "scala" tweets using [[https://github.com/DanielaSfregola/twitter4s twitter4s]]
  *   - Send tweets to Kafka using [[com.backwards.kafka.Producer Producer]]
  *   - Receive said tweets from Kafka using [[com.backwards.kafka.Consumer Consumer]]
  *   - Finally each received tweet is added to Elasticsearch using [[https://github.com/bizreach/elastic-scala-httpclient elastic-scala-httpclient]]
  */
object TwitterRunner extends App with Logging {
  val tracking = "scala"

  val twitterProducer = TwitterProducer("twitter-topic")

  val twitterBroker = new TwitterBroker
  twitterBroker.track(NonEmptyList.of(tracking))(twitterProducer.produce(_).unsafeRunSync)

  val twitterConsumer = TwitterConsumer("twitter-topic")

  val processTweets: Throwable Either Seq[(String, String)] => IO[Unit] = {
    case Left(t) =>
      IO(t.printStackTrace())

    case Right(tweets) =>
      IO(if (tweets.nonEmpty) info(s"Consumed Tweets:\n${tweets.mkString("\n")}") else info(s"No available tweets matching: $tracking")).map { _ =>
        // TODO - Actually use instead of the hardcoded PoC inside the following class
        // val elasticsearchBroker = new ElasticSearchBroker*/

        twitterConsumer doConsume processTweets
      }
  }

  twitterConsumer doConsume processTweets
}
