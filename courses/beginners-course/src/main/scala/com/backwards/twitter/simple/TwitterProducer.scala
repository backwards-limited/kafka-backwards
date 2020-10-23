package com.backwards.twitter.simple

import scala.concurrent.Future
import cats.Monad
import cats.implicits._
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.StringSerializer
import com.backwards.Or
import com.backwards.kafka.{KafkaConfig, KafkaProducer}
import com.backwards.twitter.TweetSerde.TweetSerializer
import com.danielasfregola.twitter4s.entities.Tweet

class TwitterProducer[F[_]: Monad](val kafkaProducer: KafkaProducer[F, String, Tweet]) {
  def send(tweet: Tweet): F[Throwable Or RecordMetadata] =
    kafkaProducer.send(tweet.id_str, tweet).map {
      case r @ Right(record) =>
        scribe debug s"Published tweet ${tweet.id} from Kafka partition: ${record.partition()}, offset: ${record.offset}"
        r

      case l @ Left(t) =>
        scribe.error("Publication Error", t)
        t.printStackTrace()
        l
    }
}

object TwitterProducer {
  implicit val keySerializer: StringSerializer = new StringSerializer
  implicit val valueSerializer: TweetSerializer = new TweetSerializer

  def apply(topic: String)(implicit C: KafkaConfig): TwitterProducer[Future] = {
    import scala.concurrent.ExecutionContext.Implicits.global

    new TwitterProducer(new com.backwards.kafka.future.KafkaProducer(topic))
  }
}