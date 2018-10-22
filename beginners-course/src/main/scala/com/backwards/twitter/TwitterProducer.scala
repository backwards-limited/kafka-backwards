package com.backwards.twitter

import scala.language.higherKinds
import cats.effect.Effect
import cats.implicits._
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.Serializer
import com.backwards.Or
import com.backwards.kafka.{Configuration, Producer}
import com.backwards.logging.Logging
import com.danielasfregola.twitter4s.entities.Tweet

object TwitterProducer {
  def apply[F[_]](configuration: Configuration)(implicit F: Effect[F], K: Serializer[String], V: Serializer[String]) =
    new TwitterProducer[F](configuration)(F, K, V)
}

class TwitterProducer[F[_]] private(configuration: Configuration)(implicit F: Effect[F], K: Serializer[String], V: Serializer[String]) extends Logging {
  val producer: Producer[F, String, String] = Producer[F, String, String](configuration)(F, K, V)

  val produce: Tweet => F[Throwable Or RecordMetadata] = { tweet =>
    producer.send(tweet.id.toString, tweet.text).map {
      case r @ Right(record) =>
        info(s"Published successfully: $record")
        r

      case l @ Left(t) =>
        error("Publication Error", t)
        t.printStackTrace
        l
    }
  }
}