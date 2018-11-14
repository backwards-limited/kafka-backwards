package com.backwards.twitter.simple

import scala.concurrent.Future
import scala.language.higherKinds
import cats.implicits._
import cats.{Monad, ~>}
import org.apache.kafka.clients.producer.ProducerConfig._
import org.apache.kafka.clients.producer.RecordMetadata
import com.backwards.kafka.Producer
import com.backwards.kafka.serde.Serde
import com.backwards.logging.Logging
import com.backwards.twitter.TweetSerde.TweetSerializer
import com.backwards.{Or, kafka}
import com.danielasfregola.twitter4s.entities.Tweet

object TwitterProducer {
  def apply[F[_]: Monad](topic: String) = new TwitterProducer[F](topic)
}

class TwitterProducer[F[_]: Monad](topic: String) extends Serde with Logging {
  implicit val tweetSerializer: TweetSerializer = new TweetSerializer

  val producer: Producer[F, String, Tweet] =
    Producer[F, String, Tweet](topic, kafka.config + (COMPRESSION_TYPE_CONFIG -> "snappy") + (LINGER_MS_CONFIG -> 20) + (BATCH_SIZE_CONFIG -> 32 * 1024))

  def produce(tweet: Tweet)(implicit transform: Future ~> F): F[Throwable Or RecordMetadata] =
    producer.send(tweet.id_str, tweet).map {
      case r @ Right(record) =>
        debug(s"Published tweet ${tweet.id} from Kafka partition: ${record.partition()}, offset: ${record.offset}")
        r

      case l @ Left(t) =>
        error("Publication Error", t)
        t.printStackTrace()
        l
    }
}