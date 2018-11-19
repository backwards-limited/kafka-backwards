package com.backwards.twitter.simple

import java.util.concurrent.TimeUnit
import cats.data.NonEmptyList
import cats.effect.IO
import cats.implicits._
import com.backwards.BackwardsApp
import com.backwards.elasticsearch.ElasticSearchBroker
import com.backwards.kafka.KafkaConfig
import com.backwards.twitter.Json
import com.danielasfregola.twitter4s.entities.Tweet
import com.sksamuel.elastic4s.http.ElasticDsl._

/**
  * Demo application which shows the following:
  *   - Query Twitter for "scala" and other tweets using [[https://github.com/DanielaSfregola/twitter4s twitter4s]]
  *   - Send tweets to Kafka using [[com.backwards.kafka.KafkaProducer]]
  *   - Receive said tweets from Kafka using [[com.backwards.kafka.Consumer Consumer]]
  *   - Finally each received tweet is added to Elasticsearch using [[https://github.com/bizreach/elastic-scala-httpclient elastic-scala-httpclient]]
  */
object TwitterRunner extends BackwardsApp with Json {
  implicit val kafkaConfig: KafkaConfig = com.backwards.kafka.config

  val topic = "twitter-topic"

  val twitterProducer = TwitterProducer(topic)

  val twitterBroker = new TwitterBroker
  twitterBroker.track(NonEmptyList.of("scala", "bitcoin"))(twitterProducer.send)

  val twitterConsumer = TwitterConsumer[IO](topic)

  val tweeted: Seq[(String, Tweet)] => Unit = {
    val elasticsearchBroker = new ElasticSearchBroker
    println(s"ELASTIC SLEEPING - CURRENTLY A CRAZY HACK AS WITHOUT IT WE GET A 'CONNECTION REFUSED'") // TODO
    TimeUnit.SECONDS.sleep(30)
    elasticsearchBroker.index("twitter").await

    elasticsearchBroker documentBulk ("twitter" / "tweets")
  }

  twitterConsumer.doConsume(_.unsafeRunSync)(tweeted)

  info("... and I'm spent!")
}