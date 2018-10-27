package com.backwards.twitter

import scala.language.higherKinds
import cats.effect.IO
import cats.implicits._
import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.kafka.clients.producer.RecordMetadata
import com.backwards.Or
import com.backwards.config.kafkaConfig
import com.backwards.kafka.Producer
import com.backwards.kafka.serde.Serde
import com.backwards.logging.Logging
import com.backwards.transform.Transform
import com.danielasfregola.twitter4s.entities.Tweet

object TwitterProducer {
  def apply(topic: String) = new TwitterProducer(topic)
}

class TwitterProducer private(topic: String) extends Serde.Implicits with Transform.Implicits with Logging {
  val producer: Producer[IO, String, String] = Producer[IO, String, String](topic, kafkaConfig)

  val produce: Tweet => IO[Throwable Or RecordMetadata] = { tweet =>
    producer.send(tweet.id.toString, tweet.text).map {
      case r @ Right(record) =>
        info(s"Published tweet ${tweet.id}: ${ToStringBuilder.reflectionToString(record)}")
        r

      case l @ Left(t) =>
        error("Publication Error", t)
        t.printStackTrace()
        l
    }
  }
}