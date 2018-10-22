package com.backwards.twitter

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds
import cats.data.NonEmptyList
import com.danielasfregola.twitter4s.entities.streaming.StreamingMessage
import com.danielasfregola.twitter4s.{TwitterRestClient, TwitterStreamingClient}
import com.danielasfregola.twitter4s.entities.{RatedData, StatusSearch, Tweet}
import com.danielasfregola.twitter4s.http.clients.streaming.TwitterStream

class TwitterBroker {
  sys addShutdownHook close

  lazy val client = TwitterStreamingClient()

  def close: Future[Unit] = client.shutdown

  /*def query(keywords: NonEmptyList[String])(implicit ctx: ExecutionContext): Future[RatedData[StatusSearch]] = {
    client.searchTweet(query = keywords.toList.mkString(" "))
  }*/

  def track[R](keywords: NonEmptyList[String])(tweeted: Tweet => R): Future[TwitterStream] =
    client.sampleStatuses(tracks = keywords.toList) {
      case tweet: Tweet => tweeted(tweet)
    }
}


/*


import cats.data.NonEmptyList
import com.danielasfregola.twitter4s.TwitterStreamingClient
import com.danielasfregola.twitter4s.entities.Tweet
import com.danielasfregola.twitter4s.entities.streaming.StreamingMessage
import com.danielasfregola.twitter4s.http.clients.streaming.TwitterStream

class TwitterBroker {
  val client = TwitterStreamingClient()

  def close: Future[Unit] = client.shutdown

  /*def streamTweetsToKafka[R](tweeted: Tweet => R, keywords: String*): Future[TwitterStream] = {
    val streamTweetsToKafka: Seq[String] => PartialFunction[StreamingMessage, Unit] = keywords => {
      case tweet: Tweet /*if keywords.exists(tweet.text.contains)*/ => tweeted(tweet)
    }

    client.sampleStatuses(stall_warnings = true)(streamTweetsToKafka(keywords))
  }*/

  def streamTweetsToKafka[R](tweeted: Tweet => R, keywords: NonEmptyList[String]): Future[TwitterStream] = {
    val streamTweetsToKafka: PartialFunction[StreamingMessage, Unit] = {
      case tweet: Tweet => tweeted(tweet)
    }

    client.sampleStatuses(tracks = keywords.toList)(streamTweetsToKafka)
  }
}

 */