package com.backwards.kafka.github

import scala.collection.JavaConverters._
import org.apache.kafka.common.config.ConfigDef
import org.scalatest._
import com.backwards.kafka.github.GitHubSourceConnectorConfig._

class GitHubSourceConnectorConfigSpec extends WordSpec with MustMatchers {
  trait Context {
    val configDef: ConfigDef = GitHubSourceConnectorConfig.conf

    val config: Map[String, String] = Map(
      OWNER_CONFIG -> "foo",
      REPO_CONFIG -> "bar",
      SINCE_CONFIG -> "2017-04-26T01:23:45Z",
      BATCH_SIZE_CONFIG -> "100",
      TOPIC_CONFIG -> "github-issues"
    )
  }

  "Github source connector" should {
    "check config" in new Context {
      println(conf.toRst)
    }

    "validate config" in new Context {
      configDef.validate(mapAsJavaMap(config)).stream.allMatch(_.errorMessages().isEmpty) mustEqual true
    }

    // TODO - Scalacheck ???

    "reads config correctly" in new Context {
      GitHubSourceConnectorConfig(config).authPassword mustEqual ""
    }

    "validate 'since'" in new Context {
      configDef.validateAll(mapAsJavaMap(config + (SINCE_CONFIG -> "not a date"))).get(SINCE_CONFIG).errorMessages() must not be empty
    }

    "validate batch size" in new Context {
      configDef.validateAll(mapAsJavaMap(config + (BATCH_SIZE_CONFIG -> "-1"))).get(BATCH_SIZE_CONFIG).errorMessages() must not be empty

      configDef.validateAll(mapAsJavaMap(config + (BATCH_SIZE_CONFIG -> "101"))).get(BATCH_SIZE_CONFIG).errorMessages() must not be empty
    }

    "validate user name" in new Context {
      configDef.validateAll(mapAsJavaMap(config + (AUTH_USERNAME_CONFIG -> "username"))).get(AUTH_USERNAME_CONFIG).errorMessages() mustBe empty
    }

    "validate password" in new Context {
      configDef.validateAll(mapAsJavaMap(config + (AUTH_PASSWORD_CONFIG -> "password"))).get(AUTH_PASSWORD_CONFIG).errorMessages() mustBe empty
    }
  }
}