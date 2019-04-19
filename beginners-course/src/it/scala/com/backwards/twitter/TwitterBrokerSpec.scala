package com.backwards.twitter

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.concurrent.duration._
import scala.language.postfixOps
import cats.data.NonEmptyList
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.twitter.simple.TwitterBroker
import com.danielasfregola.twitter4s.entities.Tweet

class TwitterBrokerSpec extends WordSpec with MustMatchers with ScalaFutures {
  override implicit def patienceConfig: PatienceConfig = PatienceConfig(10 seconds, 2 seconds)

  "Twitter broker" should {
    "track a term" in {
      val track = "and"
      val promise = Promise[Tweet]()

      val twitterBroker = new TwitterBroker

      twitterBroker.track(NonEmptyList.of(track))(promise.success)

      whenReady(promise.future) { tweet =>
        tweet.toString.toLowerCase must include(track)
      }
    }
  }
}