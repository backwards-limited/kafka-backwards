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

/**
  * Demo application which shows the following:
  *   - Query Twitter for "scala" and other tweets using [[https://github.com/DanielaSfregola/twitter4s twitter4s]]
  *   - Send tweets to Kafka using [[com.backwards.kafka.Producer Producer]]
  *   - Receive said tweets from Kafka using [[com.backwards.kafka.Consumer Consumer]]
  *   - Finally each received tweet is added to Elasticsearch using [[https://github.com/bizreach/elastic-scala-httpclient elastic-scala-httpclient]]
  */
object TwitterRunner extends BackwardsApp with Transform {
  val topic = "twitter-topic"

  val twitterProducer = TwitterProducer[IO](topic)

  val twitterBroker = new TwitterBroker
  twitterBroker.track(NonEmptyList.of("scala", "bitcoin"))(twitterProducer.produce(_).unsafeRunSync)

  val twitterConsumer = TwitterConsumer[IO](topic)

  val tweeted: Seq[(String, Tweet)] => Unit = {
    import com.sksamuel.elastic4s.http.ElasticDsl._

    val elasticsearchBroker = new ElasticSearchBroker

    try {
      elasticsearchBroker.client.execute {
        info("===> Creating twitter index")
        createIndex("twitter")
      }.await
    } catch {
      case t: Throwable =>
        warn(s"===> Twitter index creation: ${t.getMessage}") // Maybe index already exists
    }

    tweets => {
      info(s"Consumed Tweets:\n${tweets.mkString("\n")}")

      tweets.foreach { case (k, v) =>
        val response = elasticsearchBroker.client.execute {
          implicit val formats: Formats = Serialization formats NoTypeHints

          indexInto("twitter" / "tweets").doc(write(v))

        }.await

        println(s"===> Elastic search response = ${response.result.id}")
      }

      /*elasticsearchBroker.client.execute {
        indexInto("twitter" / "tweets").doc(jonsnow)
      }*/
    }
  }

  twitterConsumer.doConsume(tweeted)(_.unsafeRunSync)

  info("... and I'm spent!")
}