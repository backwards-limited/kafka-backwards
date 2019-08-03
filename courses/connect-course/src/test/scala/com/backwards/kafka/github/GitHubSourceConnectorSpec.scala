package com.backwards.kafka.github

import scala.collection.JavaConverters._
import org.scalatest.{MustMatchers, WordSpec}
import com.backwards.kafka.github.GitHubSourceConnectorConfig._

class GitHubSourceConnectorSpec extends WordSpec with MustMatchers {
  "Github source connector" should {
    "give only one task config" in {
      val config: Map[String, String] = Map(
        OWNER_CONFIG -> "foo",
        REPO_CONFIG -> "bar",
        SINCE_CONFIG -> "2017-04-26T01:23:45Z",
        BATCH_SIZE_CONFIG -> "100",
        TOPIC_CONFIG -> "github-issues"
      )

      val gitHubSourceConnector = new GitHubSourceConnector
      gitHubSourceConnector start mapAsJavaMap(config)

      gitHubSourceConnector.taskConfigs(1).size() mustEqual 1
      gitHubSourceConnector.taskConfigs(10).size() mustEqual 1
    }
  }
}