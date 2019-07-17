package com.backwards.twitter.simple

import scala.concurrent.Future
import scala.language.higherKinds
import cats.data.NonEmptyList
import com.danielasfregola.twitter4s.TwitterStreamingClient
import com.danielasfregola.twitter4s.entities.Tweet
import com.danielasfregola.twitter4s.http.clients.streaming.TwitterStream

class TwitterBroker {
  sys addShutdownHook close

  lazy val client = TwitterStreamingClient()

  def close: Future[Unit] = client.shutdown

  def track[R](keywords: NonEmptyList[String])(tweeted: Tweet => R): Future[TwitterStream] =
    client.filterStatuses(tracks = keywords.toList) {
      case tweet: Tweet => tweeted(tweet)
    }
}