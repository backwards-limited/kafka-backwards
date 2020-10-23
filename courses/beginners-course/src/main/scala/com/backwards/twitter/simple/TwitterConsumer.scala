package com.backwards.twitter.simple

import java.util.concurrent.TimeUnit
import scala.annotation.tailrec
import scala.language.{higherKinds, postfixOps}
import cats.Applicative
import cats.implicits._
import org.apache.kafka.clients.consumer.ConsumerConfig._
import com.backwards.kafka
import com.backwards.kafka.Consumer
import com.backwards.kafka.serde.Serdes
import com.backwards.twitter.TweetSerde.TweetDeserializer
import com.danielasfregola.twitter4s.entities.Tweet

class TwitterConsumer[F[_]: Applicative](topic: String) extends Serdes {
  implicit val tweetDeserializer: TweetDeserializer = new TweetDeserializer

  val consumer: Consumer[F, String, Tweet] =
    Consumer[F, String, Tweet](topic, kafka.config +
      (GROUP_ID_CONFIG -> "twitter-group-1") +
      (AUTO_OFFSET_RESET_CONFIG -> "latest") +
      (ENABLE_AUTO_COMMIT_CONFIG -> false) +
      (MAX_POLL_RECORDS_CONFIG -> 10) +
      (FETCH_MIN_BYTES_CONFIG -> 256))

  def consume: F[Seq[(String, Tweet)]] = consumer.poll()

  /**
    * Here is a good example of a naive approach (appropriate for this beginners course) - in a more advanced module we shall discover the likes of FS2 with Kafka.
    * @param run F[Seq[(String, Tweet)] ] => Seq[(String, Tweet)]
    * @param callback Seq[(String, Tweet)] => Unit
    */
  def doConsume(run: F[Seq[(String, Tweet)]] => Seq[(String, Tweet)])(callback: Seq[(String, Tweet)] => Unit): Unit = {
    val tweeted: Seq[(String, Tweet)] => Unit = tweets =>
      if (tweets.isEmpty) {
        scribe info "No available tweets at this moment in time....."
      } else {
        val numberOfTweets = if (tweets.size == 1) "1 Tweet" else s"${tweets.size} Tweets"
        scribe info s"Consumed $numberOfTweets: ${tweets.map(_._2.id).mkString(", ")}"

        callback(tweets.map {
          // We don't want to use the "key" set by Kafka consumer, but to use the Tweet ID as the key
          case (_, tweet) => tweet.id_str -> tweet
        })
      }

    @tailrec
    def go(): Unit = {
      tweeted(run(consume))
      scribe info "Commiting offset synchronously manually"
      consumer.underlying.commitSync()
      TimeUnit.SECONDS.sleep(10)
      go()
    }

    go()
  }
}

object TwitterConsumer {
  def apply[F[_]: Applicative](topic: String) = new TwitterConsumer[F](topic)
}