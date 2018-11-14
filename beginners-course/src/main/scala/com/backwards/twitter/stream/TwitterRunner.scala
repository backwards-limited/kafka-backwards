package com.backwards.twitter.stream

import java.util.concurrent.TimeUnit
import cats.data.NonEmptyList
import cats.effect.IO
import com.backwards.BackwardsApp
import com.backwards.elasticsearch.ElasticSearchBroker
import com.backwards.transform.Transform
import com.backwards.twitter.Json
import com.backwards.twitter.simple.{TwitterBroker, TwitterProducer}
import com.danielasfregola.twitter4s.entities.Tweet
import com.sksamuel.elastic4s.http.ElasticDsl._

/**
  * Demo application which shows the following:
  *   - Query Twitter for "scala" and other tweets using [[https://github.com/DanielaSfregola/twitter4s twitter4s]]
  *   - Send tweets to Kafka using [[com.backwards.kafka.Producer Producer]]
  *   - Consume tweets via Kafka Streams which:
  *     - filter tweets with high follower count
  *     - produce back to another topic for other consumers
  */
object TwitterRunner extends BackwardsApp with Transform with Json {
  val topic = "twitter-topic"

  val twitterProducer = TwitterProducer[IO](topic)

  val twitterBroker = new TwitterBroker
  twitterBroker.track(NonEmptyList.of("scala", "bitcoin"))(twitterProducer.produce(_).unsafeRunSync)

  /*val twitterConsumer = TwitterConsumer[IO](topic)

  val tweeted: Seq[(String, Tweet)] => Unit = {
    val elasticsearchBroker = new ElasticSearchBroker
    println(s"ELASTIC SLEEPING - CURRENTLY A CRAZY HACK AS WITHOUT IT WE GET A 'CONNECTION REFUSED'") // TODO
    TimeUnit.SECONDS.sleep(30)
    elasticsearchBroker.index("twitter").await

    elasticsearchBroker documentBulk ("twitter" / "tweets")
  }

  twitterConsumer.doConsume(_.unsafeRunSync)(tweeted)*/

  info("... and I'm spent!")
}
