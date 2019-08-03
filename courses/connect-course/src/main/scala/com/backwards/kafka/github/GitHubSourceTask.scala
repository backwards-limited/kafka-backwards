package com.backwards.kafka.github

import java.util
import java.util.concurrent.atomic.AtomicReference
import scala.collection.JavaConverters._
import org.apache.kafka.connect.source.{SourceRecord, SourceTask}

class GitHubSourceTask extends SourceTask {
  val config = new AtomicReference[GitHubSourceConnectorConfig]

  // val gitHubHttpAPIClient = new AtomicReference[GitHubAPIHttpClient]

  def start(props: util.Map[String, String]): Unit = {
    config.set(GitHubSourceConnectorConfig(props.asScala.toMap))
  }

  def poll(): util.List[SourceRecord] = ???

  def stop(): Unit = ???

  def version(): String = Version()
}