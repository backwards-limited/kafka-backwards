package com.backwards.config

import io.lemonlabs.uri.Uri

object BootstrapConfig {
  trait Bootstrap {
    def bootstrap: BootstrapConfig

    def bootstrapServers: String = bootstrap.servers.map(_.toStringRaw).mkString(",")
  }
}

case class BootstrapConfig(servers: Seq[Uri])