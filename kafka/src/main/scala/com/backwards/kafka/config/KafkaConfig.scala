package com.backwards.kafka.config

import java.util.Properties
import scala.language.implicitConversions
import io.lemonlabs.uri.Uri
import monocle.macros.syntax.lens._
import org.apache.kafka.clients.CommonClientConfigs._

object KafkaConfig {
  implicit def toProperties(c: KafkaConfig): Properties = c.toProperties

  def apply(bootstrap: BootstrapConfig): KafkaConfig =
    KafkaConfig(bootstrap, Map.empty[String, String])
}

case class KafkaConfig(bootstrap: BootstrapConfig, properties: Map[String, String] = Map.empty[String, String]) {
  lazy val bootstrapServers: String = bootstrap.servers.map(_.toStringRaw).mkString(",")

  lazy val toProperties: Properties =
    (new Properties /: (properties + (BOOTSTRAP_SERVERS_CONFIG -> bootstrapServers))) { case (p, (k, v)) =>
      p.put(k, v)
      p
    }

  def + (kv: (String, String)): KafkaConfig =
    this.lens(_.properties).modify(_ + kv)

  def + (key: String, value: String): KafkaConfig =
    this + (key -> value)

  def add(key: String, value: String): KafkaConfig =
    this + (key -> value)
}

case class BootstrapConfig(servers: Seq[Uri])