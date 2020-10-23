package com.backwards

import com.backwards.config.Config

package object elasticsearch extends Config {
  lazy val config: ElasticSearchConfig = {
    val c = load[ElasticSearchConfig]("elasticsearch")
    scribe info s"Elastic Search configuration: $c"
    c
  }
}