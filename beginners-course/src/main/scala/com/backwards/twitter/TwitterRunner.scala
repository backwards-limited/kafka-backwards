package com.backwards.twitter

import cats.data.NonEmptyList
import com.backwards.logging.Logging

/**
  * Demo application which shows the following:
  *   - Query Twitter for "scala" and other tweets using [[https://github.com/DanielaSfregola/twitter4s twitter4s]]
  *   - Send tweets to Kafka using [[com.backwards.kafka.Producer Producer]]
  *   - Receive said tweets from Kafka using [[com.backwards.kafka.Consumer Consumer]]
  *   - Finally each received tweet is added to Elasticsearch using [[https://github.com/bizreach/elastic-scala-httpclient elastic-scala-httpclient]]
  */
object TwitterRunner extends App with Logging {
  val tracking = NonEmptyList.of("scala", "bitcoin", "politics", "sport")

  val twitterProducer = TwitterProducer("twitter-topic")

  val twitterBroker = new TwitterBroker
  twitterBroker.track(tracking)(twitterProducer.produce(_).unsafeRunSync)

  val twitterConsumer = TwitterConsumer("twitter-topic")

  val processTweets: Seq[(String, String)] => Unit =
    tweets => if (tweets.isEmpty) {
      info(s"No available tweets matching: $tracking")
    } else {
      info(s"Consumed Tweets:\n${tweets.mkString("\n")}")
      // TODO - Actually use instead of the hardcoded PoC inside the following class
      // val elasticsearchBroker = new ElasticSearchBroker
    }

  twitterConsumer.doConsume(processTweets)(_.unsafeRunSync)

  info("... and I'm spent!")
}