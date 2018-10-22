package com.backwards.elasticsearch

import scala.concurrent.duration._
import scala.language.postfixOps
import jp.co.bizreach.elasticsearch4s._
import jp.co.bizreach.elasticsearch4s.retry.{FixedBackOff, RetryConfig}
import com.backwards.config.elasticSearchConfig

// TODO - Remove hardcoded hack which was a PoC
class ElasticSearchBroker {
  ESClient.using(
    elasticSearchConfig.bootstrapServers,
    retryConfig = RetryConfig(maxAttempts = 10, retryDuration = 5 seconds, backOff = FixedBackOff)
  ) { client =>
    val config = "twitter" / "tweet"

    // Insert
    client.insert(config, Tweet("takezoe", "Hello World!!"))

    // Update
    client.update(config, "1", Tweet("takezoe", "Hello Scala!!"))

    // Update partially
    client.updatePartially(config, "1", TweetMessage("Hello Japan!!"))

    // Find one document
    val tweet: Option[(String, Tweet)] = client.find[Tweet](config) { builder =>
      builder.query(termQuery("_id", "1"))
    }

    println(s"===> Find one = $tweet")

    // Search documents
    val list: ESSearchResult[Tweet] = client.list[Tweet](config) { builder =>
      builder.query(termQuery("name", "takezoe"))
    }

    println(s"===> Find all = $list")
  }
}

case class Tweet(name: String, message: String)

case class TweetMessage(message: String)