package com.backwards.twitter

import org.json4s.{Extraction, Formats, NoTypeHints, Writer}
import org.json4s.jackson.Serialization
import com.danielasfregola.twitter4s.entities.Tweet

trait Json {
  implicit val jsonFormats: Formats = Serialization formats NoTypeHints

  implicit val tweetWriter: Writer[Tweet] =
    (tweet: Tweet) => Extraction decompose tweet
}