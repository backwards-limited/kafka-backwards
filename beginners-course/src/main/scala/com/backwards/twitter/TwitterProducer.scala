package com.backwards.twitter

import scala.language.higherKinds
import cats.effect.IO
import cats.implicits._
import org.apache.kafka.clients.producer.RecordMetadata
import com.backwards.Or
import com.backwards.kafka.serde.Serde
import com.backwards.kafka.{Configuration, Producer}
import com.backwards.logging.Logging
import com.danielasfregola.twitter4s.entities.Tweet

object TwitterProducer {
  def apply(configuration: Configuration) =
    new TwitterProducer(configuration)
}

class TwitterProducer private(configuration: Configuration) extends Serde.Implicits with Logging {
  val producer: Producer[IO, String, String] = Producer[IO, String, String](configuration)

  val produce: Tweet => IO[Throwable Or RecordMetadata] = { tweet =>
    producer.send(tweet.id.toString, tweet.text).map {
      case r @ Right(record) =>
        info(s"Published successfully: $record")
        r

      case l @ Left(t) =>
        error("Publication Error", t)
        t.printStackTrace()
        l
    }
  }
}