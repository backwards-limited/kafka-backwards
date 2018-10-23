package com.backwards.config

import io.lemonlabs.uri.Uri

case class ElasticSearchConfig(bootstrap: BootstrapConfig) {
  lazy val bootstrapServers: String = bootstrap.servers.map(_.toStringRaw).mkString(",")
}

case class BootstrapConfig(servers: Seq[Uri])