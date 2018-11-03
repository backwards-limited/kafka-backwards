package com.backwards.twitter

import cats.data.NonEmptyList
import cats.effect.IO
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.write
import org.json4s.{Formats, NoTypeHints}
import com.backwards.BackwardsApp
import com.backwards.elasticsearch.ElasticSearchBroker
import com.backwards.transform.Transform
import com.danielasfregola.twitter4s.entities.Tweet
import com.sksamuel.elastic4s.http.ElasticDsl._

/**
  * Demo application which shows the following:
  *   - Query Twitter for "scala" and other tweets using [[https://github.com/DanielaSfregola/twitter4s twitter4s]]
  *   - Send tweets to Kafka using [[com.backwards.kafka.Producer Producer]]
  *   - Receive said tweets from Kafka using [[com.backwards.kafka.Consumer Consumer]]
  *   - Finally each received tweet is added to Elasticsearch using [[https://github.com/bizreach/elastic-scala-httpclient elastic-scala-httpclient]]
  */
object TwitterRunner extends BackwardsApp with Transform {
  implicit val jsonFormats: Formats = Serialization formats NoTypeHints

  val topic = "twitter-topic"

  val twitterProducer = TwitterProducer[IO](topic)

  val twitterBroker = new TwitterBroker
  twitterBroker.track(NonEmptyList.of("scala", "bitcoin"))(twitterProducer.produce(_).unsafeRunSync)

  val twitterConsumer = TwitterConsumer[IO](topic)

  val tweeted: Seq[(String, Tweet)] => Unit = {
    val elasticsearchBroker = new ElasticSearchBroker
    elasticsearchBroker.index("twitter").await

    tweets => {
      tweets.foreach { case (key, tweet) =>
        val response = elasticsearchBroker.client.execute {
          indexInto("twitter" / "tweets") id tweet.id_str doc write(tweet)
        }.await

        info(s"Elastic search response = ${response.result.id}")
      }
    }
  }

  twitterConsumer.doConsume(tweeted)(_.unsafeRunSync)

  info("... and I'm spent!")
}