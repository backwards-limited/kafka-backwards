package com.backwards

import com.backwards.config.Config
import com.backwards.logging.Logging

package object elasticsearch extends Config with Logging {
  lazy val config: ElasticSearchConfig = {
    val c = load[ElasticSearchConfig]("elasticsearch")
    info(s"Elastic Search configuration: $c")
    c
  }
}