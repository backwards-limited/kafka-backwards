package com.backwards

import com.backwards.logging.Logging

package object config extends ConfigOps with Logging {
  lazy val kafkaConfig: KafkaConfig = {
    val c = load[KafkaConfig]("kafka")
    logger info s"Kafka configuration: $c"
    c
  }

  lazy val elasticSearchConfig: ElasticSearchConfig = {
    val c = load[ElasticSearchConfig]("elasticsearch")
    logger info s"Elastic Search configuration: $c"
    c
  }
}