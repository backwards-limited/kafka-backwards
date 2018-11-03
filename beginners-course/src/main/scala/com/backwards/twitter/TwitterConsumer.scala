package com.backwards.twitter

import scala.annotation.tailrec
import scala.language.{higherKinds, postfixOps}
import cats.Applicative
import cats.implicits._
import org.apache.kafka.clients.consumer.ConsumerConfig.{AUTO_OFFSET_RESET_CONFIG, GROUP_ID_CONFIG}
import com.backwards.config.kafkaConfig
import com.backwards.kafka.Consumer
import com.backwards.kafka.serde.Serde
import com.backwards.logging.Logging
import com.backwards.twitter.TweetSerde.TweetDeserializer
import com.danielasfregola.twitter4s.entities.Tweet

object TwitterConsumer {
  def apply[F[_]: Applicative](topic: String) = new TwitterConsumer[F](topic)
}

class TwitterConsumer[F[_]: Applicative](topic: String) extends Serde with Logging {
  implicit val tweetDeserializer: TweetDeserializer = new TweetDeserializer

  val consumer: Consumer[F, String, Tweet] =
    Consumer[F, String, Tweet](topic, kafkaConfig + (GROUP_ID_CONFIG, "twitter-group-1") + (AUTO_OFFSET_RESET_CONFIG -> "latest")) // TODO - config: max.poll.records

  def consume: F[Seq[(String, Tweet)]] = consumer.poll()

  /**
    * Here is a good example of a naive approach (appropriate for this beginners course) - in a more advanced module we shall discover the likes of FS2 with Kafka.
    * @param callback Seq[(String, Tweet)] => Unit
    * @param run F[Seq[(String, Tweet)] ] => Seq[(String, Tweet)]
    */
  def doConsume(callback: Seq[(String, Tweet)] => Unit)(run: F[Seq[(String, Tweet)]] => Seq[(String, Tweet)]): Unit = {
    val tweeted: Seq[(String, Tweet)] => Unit = tweets =>
      if (tweets.isEmpty) info("No available tweets at this moment in time.....")
      else callback(tweets)

    @tailrec
    def go(): Unit = {
      tweeted(run(consume))
      go()
    }

    go()
  }
}