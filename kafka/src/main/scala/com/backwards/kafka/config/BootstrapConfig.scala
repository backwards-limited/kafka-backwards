package com.backwards.kafka.config

import io.lemonlabs.uri.Uri

case class BootstrapConfig(servers: Seq[Uri])