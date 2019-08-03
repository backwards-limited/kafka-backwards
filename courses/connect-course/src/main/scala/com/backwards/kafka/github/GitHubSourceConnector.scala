package com.backwards.kafka.github

import java.util
import java.util.concurrent.atomic.AtomicReference
import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.connect.connector.Task
import org.apache.kafka.connect.source.SourceConnector
import scala.collection.JavaConverters._

class GitHubSourceConnector extends SourceConnector {
  private val githubSourceConnectorConfig = new AtomicReference[GitHubSourceConnectorConfig]()

  def start(props: util.Map[String, String]): Unit =
    githubSourceConnectorConfig set GitHubSourceConnectorConfig(props.asScala.toMap)

  def taskClass(): Class[_ <: Task] = ???

  def taskConfigs(maxTasks: Int): util.List[util.Map[String, String]] =
    List(githubSourceConnectorConfig.get.originalsStrings()).asJava

  def stop(): Unit = {
    // Do things that are necessary to stop your connector - nothing is necessary to stop for this connector
  }

  def config(): ConfigDef = GitHubSourceConnectorConfig.conf

  def version(): String = Version()
}