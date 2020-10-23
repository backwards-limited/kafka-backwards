package com.backwards

import org.apache.kafka.clients.CommonClientConfigs.RETRIES_CONFIG
import com.backwards.config.Config

package object kafka extends Config {
  lazy val config: KafkaConfig = {
    val c = load[KafkaConfig]("kafka") + (RETRIES_CONFIG -> Int.MaxValue) // TODO - Can we configure Int.MaxValue?

    scribe info s"Kafka configuration: $c"
    c
  }
}