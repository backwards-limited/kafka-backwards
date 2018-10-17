package com.backwards.twitter

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds
import cats.data.NonEmptyList
import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{RatedData, StatusSearch}

class TwitterBroker {
  sys addShutdownHook close

  lazy val client = TwitterRestClient()

  def close: Future[Unit] = client.shutdown

  def query(keywords: NonEmptyList[String])(implicit ctx: ExecutionContext): Future[RatedData[StatusSearch]] = {
    client.searchTweet(query = keywords.toList.mkString(" "))
  }
}