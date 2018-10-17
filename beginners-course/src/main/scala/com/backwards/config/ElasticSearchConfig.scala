package com.backwards.config

import io.lemonlabs.uri.Uri

case class ElasticSearchConfig(servers: Seq[Uri])