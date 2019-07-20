package com.backwards.kafka.github

import java.time.{Instant, ZonedDateTime}
import scala.collection.JavaConverters._
import org.apache.kafka.common.config.ConfigDef.{Importance, Type}
import org.apache.kafka.common.config.{AbstractConfig, ConfigDef}
import com.backwards.kafka.github.GitHubSourceConnectorConfig._

class GitHubSourceConnectorConfig(config: ConfigDef, parsedConfig: Map[String, String]) extends AbstractConfig(config, mapAsJavaMap(parsedConfig)) {
  def ownerConfig: String = getString(OWNER_CONFIG)

  def repoConfig: String = getString(REPO_CONFIG)

  def batchSize: Int = getInt(BATCH_SIZE_CONFIG)

  def since: Instant = Instant.parse(getString(SINCE_CONFIG))

  def topic: String = getString(TOPIC_CONFIG)

  def authUsername: String = getString(AUTH_USERNAME_CONFIG)

  def authPassword: String = getPassword(AUTH_PASSWORD_CONFIG).value
}

object GitHubSourceConnectorConfig {
  val TOPIC_CONFIG = "topic"
  private val TOPIC_DOC = "Topic to write to"

  val OWNER_CONFIG = "github.owner"
  private val OWNER_DOC = "Owner of the repository you'd like to follow"

  val REPO_CONFIG = "github.repo"
  private val REPO_DOC = "Repository you'd like to follow"

  val SINCE_CONFIG = "since.timestamp"
  private val SINCE_DOC =
    """
      |Only issues updated at or after this time are returned.
      |This is a timestamp in ISO 8601 format: YYYY-MM-DDTHH:MM:SSZ.
      |Defaults to a year from first launch.
    """.stripMargin

  val BATCH_SIZE_CONFIG = "batch.size"
  private val BATCH_SIZE_DOC = "Number of data points to retrieve at a time. Defaults to 100 (max value)"

  val AUTH_USERNAME_CONFIG = "auth.username"
  private val AUTH_USERNAME_DOC = "Optional Username to authenticate calls"

  val AUTH_PASSWORD_CONFIG = "auth.password"
  private val AUTH_PASSWORD_DOC = "Optional Password to authenticate calls"

  def apply(parsedConfig: Map[String, String]) = new GitHubSourceConnectorConfig(conf, parsedConfig)

  def conf: ConfigDef = (new ConfigDef)
    .define(TOPIC_CONFIG, Type.STRING, Importance.HIGH, TOPIC_DOC)
    .define(OWNER_CONFIG, Type.STRING, Importance.HIGH, OWNER_DOC)
    .define(REPO_CONFIG, Type.STRING, Importance.HIGH, REPO_DOC)
    .define(BATCH_SIZE_CONFIG, Type.INT, 100, new BatchSizeValidator, Importance.LOW, BATCH_SIZE_DOC)
    .define(SINCE_CONFIG, Type.STRING, ZonedDateTime.now.minusYears(1).toInstant.toString, new TimestampValidator, Importance.HIGH, SINCE_DOC)
    .define(AUTH_USERNAME_CONFIG, Type.STRING, "", Importance.HIGH, AUTH_USERNAME_DOC)
    .define(AUTH_PASSWORD_CONFIG, Type.PASSWORD, "", Importance.HIGH, AUTH_PASSWORD_DOC)
}